package cn.dpc.ab.api.ai.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.ai.chat.messages.Message;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Other chat handler.
 */
public interface ChatHandler {

    /**
     * Handle chat message.
     *
     * @param clientId the client id
     * @param message  the message
     * @param history  the history
     * @return the flux
     * @throws JsonProcessingException the json processing exception
     */
    Flux<String> handle(String clientId, String message, List<Message> history) throws JsonProcessingException;
}
