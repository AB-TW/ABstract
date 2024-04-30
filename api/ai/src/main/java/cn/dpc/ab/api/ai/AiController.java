package cn.dpc.ab.api.ai;

import cn.dpc.ab.api.ai.bot.ChatBot;
import cn.dpc.ab.api.ai.bot.ChatbotMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Controller for AI module.
 */
@RestController
public class AiController {

    private final ChatBot chatBot;

    public AiController(ChatBot chatBot) {
        this.chatBot = chatBot;
    }

    /**
     * Chat with AI.
     *
     * @param message the message
     * @return the flux
     */
    @GetMapping("/ai")
    public Flux<String> ai(@RequestParam(value = "message", required = false, defaultValue = "What is ABstract?") String message,
                           @RequestHeader(value = "x-client-id") String clientId) throws JsonProcessingException {

        return chatBot.chat(clientId, message);
    }

    /**
     * History list.
     *
     * @param clientId the client id
     * @return the list
     */
    @GetMapping("/ai/history")
    public List<ChatbotMessage> history(@RequestHeader(value = "x-client-id") String clientId) {
        return chatBot.getHistory(clientId);
    }

    /**
     * Clear history.
     *
     * @param clientId the client id
     */
    @PostMapping("/ai/history/clear")
    public void clearHistory(@RequestHeader(value = "x-client-id") String clientId) {
        chatBot.clearHistory(clientId);
    }
}
