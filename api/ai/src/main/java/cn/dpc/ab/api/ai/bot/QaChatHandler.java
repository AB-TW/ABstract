package cn.dpc.ab.api.ai.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.joining;

/**
 * QA chat handler.
 */
public class QaChatHandler extends AbstractChatHandler {
    private final SimpleVectorStore simpleVectorStore;
    private final Resource systemResource;
    private final ObjectMapper objectMapper;

    /**
     * Instantiates a new Qa chat handler.
     *
     * @param streamingChatModel the streaming chat client
     * @param chatModel          the chat client
     * @param simpleVectorStore   the simple vector store
     * @param systemResource      the system resource
     * @param objectMapper        the object mapper
     */
    public QaChatHandler(StreamingChatModel streamingChatModel,
                         ChatModel chatModel,
                         SimpleVectorStore simpleVectorStore,
                         Resource systemResource,
                         ObjectMapper objectMapper) {
        super(streamingChatModel, chatModel);
        this.simpleVectorStore = simpleVectorStore;
        this.systemResource = systemResource;
        this.objectMapper = objectMapper;
    }

    @Override
    public Flux<String> handle(String clientId, String message, List<Message> history) {
        List<Document> similarDocs = simpleVectorStore.similaritySearch(SearchRequest.query(message).withSimilarityThreshold(0.8F)
                .withTopK(5)
        );
        String context = similarDocs.stream()
                .map(Document::getContent)
                .collect(joining(System.lineSeparator()));
        var systemPromptTemplate = new SystemPromptTemplate(systemResource);

        var systemMessage = systemPromptTemplate.createMessage(Map.of("context", context));
        var userPromptTemplate = new PromptTemplate("{query}");
        var userMessage = userPromptTemplate.createMessage(Map.of("query", message));
        var messages = new ArrayList<Message>();
        messages.add(systemMessage);
        messages.addAll(history);
        messages.add(userMessage);
        var prompt = new Prompt(messages,
                OpenAiChatOptions.builder()
                        .withFunctions(Set.of("countFeature", "getFeatureByFeatureKey"))
                        .build());
        history.add(userMessage);

        String sources;
        try {
            sources = similarDocs.isEmpty() ? "" : "#Sources:" + objectMapper.writeValueAsString(similarDocs.stream()
                    .map(Document::getContent)
                    .toList());
        } catch (JsonProcessingException e) {
            sources = "";
        }

        return streamChat(history, prompt, sources);
    }
}
