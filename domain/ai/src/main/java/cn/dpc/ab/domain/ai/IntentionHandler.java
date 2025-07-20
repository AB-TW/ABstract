package cn.dpc.ab.domain.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static cn.dpc.ab.domain.ai.AiConsts.INTENTION;

/**
 * IntentionHandler.
 * */
@RequiredArgsConstructor
@Slf4j
public class IntentionHandler {
    private final ObjectMapper objectMapper;
    private final ChatClient chatClient;
    private final AiProperties aiProperties;

    /**
     * Gets intention by message.
     *
     * @param message            the message
     * @param historyVectorStore the history vector store
     * @return the intention by message
     */
    public Intention getIntentionByMessage(String message, SimpleVectorStore historyVectorStore) throws JsonProcessingException {
        List<Document> intentions = historyVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .similarityThreshold(aiProperties.getIntentSimilarityThreshold())
                        .topK(5)
                        .build());
        if (intentions.isEmpty()) {
            return Intention.OTHER;
        }

        String template = """
                candidates is an array of text.
                Please compare each item in candidates with the content in input \
                one by one and provide a similarity score between 0 and 1. \
                And Then Output the index of the item with the highest similarity score.
                Provide the result directly without outputting the steps.
                                
                candidates：
                {candidates}
                                
                input:
                {input}""";

        record IndexResult(int index) {
        }

        var indexResult = new IndexResult(0);
        try {
            indexResult = chatClient.prompt()
                    .user(u -> {
                        try {
                            u.text(template)
                                    .param("candidates", objectMapper.writeValueAsString(
                                            intentions
                                                    .stream()
                                                    .map(Document::getText).toList()))
                                    .param("input", message);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }).call()
                    .entity(IndexResult.class);
        } catch (Exception e) {
            log.error("rerank intention failed", e);
        }

        var intention = intentions.get(Optional.ofNullable(indexResult)
                .filter(item -> item.index >= 0 && item.index < intentions.size())
                .map(IndexResult::index)
                .orElse(0));

        return Arrays.stream(Intention.values())
                .filter(item -> item.name().equalsIgnoreCase(String.valueOf(intention.getMetadata().get(INTENTION))))
                .findFirst()
                .orElse(Intention.QA);
    }

}
