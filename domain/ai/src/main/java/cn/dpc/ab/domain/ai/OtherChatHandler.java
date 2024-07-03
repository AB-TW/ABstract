package cn.dpc.ab.domain.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.ai.chat.messages.Message;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Other chat handler.
 */
public class OtherChatHandler implements ChatHandler {

    @Override
    public Flux<String> handle(String clientId, String message, List<Message> history) throws JsonProcessingException  {
        return Flux.just("抱歉，暂时无法回答您的问题。");
    }
}
