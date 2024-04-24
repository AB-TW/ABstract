package cn.dpc.ab.api.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.joining;

/**
 * Controller for AI module.
 */
@RestController
@RequiredArgsConstructor
public class AiController {

    private final OpenAiChatClient chatClient;
    private final SimpleVectorStore simpleVectorStore;

    /**
     * Chat with AI.
     *
     * @param message the message
     * @return the flux
     */
    @GetMapping("/ai")
    public Flux<String> ai(@RequestParam(value = "message", required = false, defaultValue = "What is ABstract?") String message) {
        List<Document> similarDocs = simpleVectorStore.similaritySearch(message);
        String information = similarDocs.stream()
                .map(Document::getContent)
                .collect(joining(System.lineSeparator()));

        var systemPromptTemplate = new SystemPromptTemplate("""
                You are a helpful assistant.
                Use only the following information to answer the question.
                Do not use any other information. If you do not know, simply answer: Unknown .

                {information}
                """);

        var systemMessage = systemPromptTemplate.createMessage(Map.of("information", information));
        var userPromptTemplate = new PromptTemplate("{query}");
        var userMessage = userPromptTemplate.createMessage(Map.of("query", message));
        var prompt = new Prompt(List.of(systemMessage, userMessage),
                OpenAiChatOptions.builder()
                        .withFunctions(Set.of("addFeature", "countFeature", "getFeatureByFeatureKey"))
                        .build());
        return chatClient.stream(prompt)
                .map(response -> Optional.ofNullable(response.getResult())
                        .map(Generation::getOutput)
                        .map(AbstractMessage::getContent)
                        .orElse(""));
    }
}
