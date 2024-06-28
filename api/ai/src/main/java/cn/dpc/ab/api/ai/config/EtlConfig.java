package cn.dpc.ab.api.ai.config;

import cn.dpc.ab.domain.ai.MarkDownTextSplitter;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration for ETL module.
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "ai.enabled", havingValue = "true")
public class EtlConfig {
    private final SimpleVectorStore simpleVectorStore;

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
