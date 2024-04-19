package cn.dpc.ab.api.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.joining;

/**
 * Controller for AI module.
 */
@RestController
@RequiredArgsConstructor
public class AiController {

    @Value("classpath:/prompts/system-message.st")
    private Resource systemResource;
    private final Map<String, List<Message>> histories = new ConcurrentHashMap<>();

    private final OpenAiChatClient chatClient;
    private final SimpleVectorStore simpleVectorStore;
    private final ObjectMapper objectMapper;

    /**
     * Chat with AI.
     *
     * @param message the message
     * @return the flux
     */
    @GetMapping("/ai")
    public Flux<String> ai(@RequestParam(value = "message", required = false, defaultValue = "What is ABstract?") String message,
                           @RequestHeader(value = "x-client-id") String clientId) {
        List<Message> history = histories.getOrDefault(clientId, new ArrayList<>());
        if (history.size() > 20) {
            history.remove(0);
        }
        histories.put(clientId, history);
        List<Document> similarDocs = simpleVectorStore.similaritySearch(SearchRequest.query(message).withSimilarityThreshold(0.8F));
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
                        .withFunctions(Set.of("addFeature", "countFeature", "getFeatureByFeatureKey"))
                        .build());
        history.add(userMessage);
        StringBuilder assistantMessage = new StringBuilder();

        String sources;
        try {
            sources = similarDocs.isEmpty() ? "" : "#Sources:" + objectMapper.writeValueAsString(similarDocs.stream()
                    .map(Document::getContent)
                    .toList());
        } catch (JsonProcessingException e) {
            sources = "";
        }

        return chatClient.stream(prompt)
                .map(response -> Optional.ofNullable(response.getResult())
                        .map(Generation::getOutput)
                        .map(resultMessage -> {
                            if (null != resultMessage.getContent()) {
                                assistantMessage.append(resultMessage.getContent());
                            }
                            return resultMessage.getContent();
                        })
                        .orElse(""))
                .concatWith(Mono.just(sources))
                .doOnComplete(() -> history.add(new AssistantMessage(assistantMessage.toString())));
    }

    /**
     * History list.
     *
     * @param clientId the client id
     * @return the list
     */
    @GetMapping("/ai/history")
    public List<RobotMessage> history(@RequestHeader(value = "x-client-id") String clientId) {
        return histories.getOrDefault(clientId, new ArrayList<>())
                .stream()
                .map(resultMessage -> {
                    if (resultMessage.getMessageType() == MessageType.USER) {
                        return new RobotMessage("user", resultMessage.getContent());
                    }
                    return new RobotMessage("bot", resultMessage.getContent());
                })
                .toList();
    }

    /**
     * Clear history.
     *
     * @param clientId the client id
     */
    @PostMapping("/ai/history/clear")
    public void clearHistory(@RequestHeader(value = "x-client-id") String clientId) {
        histories.remove(clientId);
    }

    /**
     * The record Robot message.
     */
    public record RobotMessage(String sender, String text) {
    }
}
