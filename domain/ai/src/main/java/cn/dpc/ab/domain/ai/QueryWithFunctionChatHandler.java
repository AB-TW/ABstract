package cn.dpc.ab.domain.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * QA chat handler.
 */
public class QueryWithFunctionChatHandler extends AbstractChatHandler {
    private final ChatOptions chatOptions;

    /**
     * Instantiates a new Qa chat handler.
     *
     * @param streamingChatModel the streaming chat client
     * @param chatModel          the chat client
     * @param chatOptions        the chat options may with function calling
     */
    public QueryWithFunctionChatHandler(StreamingChatModel streamingChatModel,
                                        ChatModel chatModel,
                                        ChatOptions chatOptions) {
        super(streamingChatModel, chatModel);
        this.chatOptions = chatOptions;
    }

    @Override
    public Flux<String> handle(String clientId, String message, List<Message> history) throws JsonProcessingException {
        var userPromptTemplate = new PromptTemplate("{query}");
        var userMessage = userPromptTemplate.createMessage(Map.of("query", message));
        var messages = new ArrayList<Message>();
        messages.add(userMessage);
        var prompt = new Prompt(messages, chatOptions);
        history.add(userMessage);

        return streamChat(history, prompt, "");
    }
}
