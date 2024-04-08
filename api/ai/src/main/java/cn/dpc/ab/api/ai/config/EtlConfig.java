package cn.dpc.ab.api.ai.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration for ETL module.
 */
@Component
@RequiredArgsConstructor
public class EtlConfig {
    private final OpenAiChatClient chatClient;
    private final SimpleVectorStore simpleVectorStore;

    /**
     * Initialize vector store.
     */
    @PostConstruct
    public void initVectorStore() {
        TikaDocumentReader textReader = new TikaDocumentReader("classpath:/docs/ABstract.md");
        textReader.get().forEach(doc -> doc.getMetadata().put("filename", "docs/ABstract.md"));


        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(chatClient, 3);

        TextSplitter textSplitter = new TokenTextSplitter();

        List<Document> documents = keywordMetadataEnricher
                .apply(textSplitter
                        .apply(textReader.get()));

        simpleVectorStore.add(documents);
    }

}
