package cn.dpc.ab.api.ai.bot;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * Abstract chat handler.
 */
public abstract class AbstractChatHandler implements ChatHandler {

    protected final StreamingChatClient streamingChatClient;
    protected final ChatClient chatClient;

    public AbstractChatHandler(StreamingChatClient streamingChatClient, ChatClient chatClient) {
        this.streamingChatClient = streamingChatClient;
        this.chatClient = chatClient;
    }

    protected Flux<String> streamChat(List<Message> history, Prompt prompt, String sources) {
        StringBuilder assistantMessage = new StringBuilder();
        return streamingChatClient.stream(prompt)
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
}
