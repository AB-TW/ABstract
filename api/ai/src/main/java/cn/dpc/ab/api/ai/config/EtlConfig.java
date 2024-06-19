package cn.dpc.ab.api.ai.config;

import cn.dpc.ab.api.ai.bot.Intention;
import groovy.util.logging.Slf4j;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration for ETL module.
 */
@Component
@Slf4j
public class EtlConfig {
    private static final Logger log = LoggerFactory.getLogger(EtlConfig.class);
    private final SimpleVectorStore simpleVectorStore;
    public static final String INTENTION = "intention";

    /**
     * Instantiates a new Etl config.
     *
     * @param simpleVectorStore the simple vector store
     */
    public EtlConfig(@Qualifier("simpleVectorStore") SimpleVectorStore simpleVectorStore
    ) {
        this.simpleVectorStore = simpleVectorStore;
    }

    /**
     * Initialize vector store.
     */
    @PostConstruct
    public void init() {
        this.initDocVectorStore();
    }

    private void initDocVectorStore() {
        TextReader textReader = new TextReader("classpath:/docs/ABstract.md");
        textReader.get().forEach(doc -> doc.getMetadata().put("filename", "docs/ABstract.md"));

        TextSplitter textSplitter = new MarkDownTextSplitter();

        List<Document> documents = textSplitter
                .apply(textReader.get());

        try {
            simpleVectorStore.add(documents);
        } catch (Exception e) {
            log.error("Failed to add documents to vector store", e);
        }
    }

}
