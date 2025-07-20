package cn.dpc.ab.domain.ai;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * Abstract chat handler.
 */
public abstract class AbstractChatHandler implements ChatHandler {

    protected final StreamingChatModel streamingChatClient;
    protected final ChatModel chatModel;

    public AbstractChatHandler(StreamingChatModel streamingChatClient, ChatModel chatModel) {
        this.streamingChatClient = streamingChatClient;
        this.chatModel = chatModel;
    }

    protected Flux<String> streamChat(List<Message> history, Prompt prompt, String sources) {
        StringBuilder assistantMessage = new StringBuilder();
        return streamingChatClient.stream(prompt)
                .map(response -> Optional.ofNullable(response.getResult())
                        .map(Generation::getOutput)
                        .map(resultMessage -> {
                            if (null != resultMessage.getText()) {
                                assistantMessage.append(resultMessage.getText());
                            }
                            return resultMessage.getText();
                        })
                        .orElse(""))
                .concatWith(Mono.just(sources))
                .doOnComplete(() -> history.add(new AssistantMessage(assistantMessage.toString())));
    }
}
