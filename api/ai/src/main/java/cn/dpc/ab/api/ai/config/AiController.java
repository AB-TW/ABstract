package cn.dpc.ab.api.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Optional;

/**
 * Controller for AI module.
 */
@RestController
@RequiredArgsConstructor
public class AiController {

    private final OpenAiChatClient chatClient;

    /**
     * Chat with AI.
     *
     * @param message the message
     * @return the flux
     */
    @GetMapping("/ai")
    public Flux<String> ai(@RequestParam(value = "message", required = false, defaultValue = "What is AB Testing?") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatClient.stream(prompt)
                .map(response -> Optional.ofNullable(response.getResult())
                        .map(Generation::getOutput)
                        .map(AbstractMessage::getContent)
                        .orElse(""));
    }
}
