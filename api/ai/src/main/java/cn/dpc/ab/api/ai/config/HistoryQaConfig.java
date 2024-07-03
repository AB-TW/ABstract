package cn.dpc.ab.api.ai.config;

import cn.dpc.ab.domain.ai.Intention;
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

import static cn.dpc.ab.domain.ai.AiConsts.INTENTION;

/**
 * Configuration for ETL module.
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "ai.enabled", havingValue = "true")
public class HistoryQaConfig {
    private final SimpleVectorStore historyVectorStore;

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
        this.initHistoryVectorStore("classpath:/docs/history-query-with-function.md", Intention.QUERY_WITH_FUNCTION.name());
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
