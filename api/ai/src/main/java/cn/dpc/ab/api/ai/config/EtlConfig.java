package cn.dpc.ab.api.ai.config;

import cn.dpc.ab.api.ai.bot.Intention;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration for ETL module.
 */
@Component
public class EtlConfig {
    private final OpenAiChatClient chatClient;
    private final SimpleVectorStore simpleVectorStore;
    private final SimpleVectorStore historyVectorStore;
    public static final String INTENTION = "intention";

    /**
     * Instantiates a new Etl config.
     *
     * @param chatClient         the chat client
     * @param simpleVectorStore  the simple vector store
     * @param historyVectorStore the history vector store
     */
    public EtlConfig(OpenAiChatClient chatClient, @Qualifier("simpleVectorStore") SimpleVectorStore simpleVectorStore,
                     @Qualifier("historyVectorStore") SimpleVectorStore historyVectorStore) {
        this.chatClient = chatClient;
        this.simpleVectorStore = simpleVectorStore;
        this.historyVectorStore = historyVectorStore;
    }

    /**
     * Initialize vector store.
     */
    @PostConstruct
    public void initVectorStore() {
        initDocVectorStore();
        initHistoryVectorStore("classpath:/docs/history.md", Intention.QA.name());
        initHistoryVectorStore("classpath:/docs/history-add-feature.md", Intention.ADD_FEATURE.name());
    }

    private void initDocVectorStore() {
        TextReader textReader = new TextReader("classpath:/docs/ABstract.md");
        textReader.get().forEach(doc -> doc.getMetadata().put("filename", "docs/ABstract.md"));


        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(chatClient, 3);

        TextSplitter textSplitter = new MarkDownTextSplitter();

        List<Document> documents = keywordMetadataEnricher
                .apply(textSplitter
                        .apply(textReader.get()));

        simpleVectorStore.add(documents);
    }

    private void initHistoryVectorStore(String sourceLink, String intention) {
        TextReader textReader = new TextReader(sourceLink);
        textReader.get().forEach(doc -> doc.getMetadata().put(INTENTION, intention));


        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(chatClient, 3);

        TextSplitter textSplitter = new MarkDownTextSplitter();

        List<Document> documents = keywordMetadataEnricher
                .apply(textSplitter
                        .apply(textReader.get()));

        historyVectorStore.add(documents);
    }

}
