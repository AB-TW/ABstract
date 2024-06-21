package cn.dpc.ab.api.ai.config;

import cn.dpc.ab.api.ai.bot.Intention;
import groovy.util.logging.Slf4j;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class HistoryQaConfig {
    private static final Logger log = LoggerFactory.getLogger(HistoryQaConfig.class);
    private final SimpleVectorStore historyVectorStore;
    public static final String INTENTION = "intention";

    /**
     * Instantiates a new Etl config.
     *
     * @param historyVectorStore the history vector store
     */
    public HistoryQaConfig(@Qualifier("historyVectorStore") SimpleVectorStore historyVectorStore) {
        this.historyVectorStore = historyVectorStore;
    }

    /**
     * Initialize vector store.
     */
    @PostConstruct
    public void init() {
        this.initHistoryVectorStore("classpath:/docs/history.md", Intention.QA.name());
        this.initHistoryVectorStore("classpath:/docs/history-add-feature.md", Intention.ADD_FEATURE.name());
    }

    private void initHistoryVectorStore(String sourceLink, String intention) {
        TextReader textReader = new TextReader(sourceLink);
        textReader.get().forEach(doc -> doc.getMetadata().put(INTENTION, intention));


        TextSplitter textSplitter = new MarkDownTextSplitter();

        List<Document> documents = textSplitter
                .apply(textReader.get());

        try {
            historyVectorStore.add(documents);
        } catch (Exception e) {
            log.error("Failed to add documents to vector store", e);
        }
    }

}
