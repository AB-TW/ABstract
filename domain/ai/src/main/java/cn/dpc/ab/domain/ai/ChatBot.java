package cn.dpc.ab.domain.ai;

import cn.dpc.ab.domain.feature.Features;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static cn.dpc.ab.domain.ai.Intention.OTHER;
import static cn.dpc.ab.domain.ai.Intention.QA;


/**
 * The type Chat bot.
 */
public class ChatBot {


    private final SimpleVectorStore historyVectorStore;
    private final AddFeatureChatHandler addFeatureChatHandler;
    private final QaChatHandler qaChatHandler;
    private final OtherChatHandler otherChatHandler;
    private final boolean enabled;

    /**
     * Instantiates a new cn.dpc.ab.domain.ai.ChatBot.
     *
     * @param systemResource     the system resource
     * @param streamingChatModel the stream chat client
     * @param chatModel          the chat client
     * @param qaVectorStore      the qa vector store
     * @param objectMapper       the object mapper
     * @param historyVectorStore the history vector store
     * @param features           the features
     * @param chatOptions        the chat options
     * @param enabled            the enabled
     */
    public ChatBot(Resource systemResource,
                   StreamingChatModel streamingChatModel,
                   ChatModel chatModel,
                   SimpleVectorStore qaVectorStore,
                   ObjectMapper objectMapper,
                   SimpleVectorStore historyVectorStore,
                   Features features,
                   ChatOptions chatOptions,
                   boolean enabled) {
        this.historyVectorStore = historyVectorStore;
        this.addFeatureChatHandler = new AddFeatureChatHandler(streamingChatModel, chatModel, objectMapper, features);
        this.qaChatHandler = new QaChatHandler(streamingChatModel, chatModel, qaVectorStore, systemResource,
                chatOptions, objectMapper);
        this.otherChatHandler = new OtherChatHandler();
        this.enabled = enabled;
    }

    /**
     * Chat with bot.
     *
     * @param clientId the client id
     * @param message  the message
     * @return the robot message
     */
    public Flux<String> chat(String clientId, String message) throws JsonProcessingException {
        if (!this.enabled) {
            return Flux.just("抱歉，暂时无法回答您的问题。");
        }
        List<Message> history = getHistoryOfClient(clientId);

        var intention = Intention.getIntentionByMessage(message, historyVectorStore);
        if (intention != OTHER && intention != QA) {
            ChatStore.globalIntention.put(clientId, intention);
        }

        if (intention == OTHER) {
            intention = ChatStore.globalIntention.getOrDefault(clientId, OTHER);
        }

        switch (intention) {
            case QA -> {
                return qaChatHandler.handle(clientId, message, history);
            }
            case ADD_FEATURE -> {
                return addFeatureChatHandler.handle(clientId, message, history);
            }
            default -> {
                return otherChatHandler.handle(clientId, message, history);
            }
        }
    }

    private List<Message> getHistoryOfClient(String clientId) {
        List<Message> history = ChatStore.histories.getOrDefault(clientId, new ArrayList<>());
        if (history.size() > 20) {
            history.remove(0);
        }
        ChatStore.histories.put(clientId, history);
        return history;
    }

    /**
     * Get chat history.
     *
     * @param clientId the client id
     * @return the history messages
     */
    public List<ChatbotMessage> getHistory(String clientId) {
        return ChatStore.histories.getOrDefault(clientId, new ArrayList<>())
                .stream()
                .map(resultMessage -> {
                    if (resultMessage.getMessageType() == MessageType.USER) {
                        return new ChatbotMessage("user", resultMessage.getContent());
                    }
                    return new ChatbotMessage("bot", resultMessage.getContent());
                })
                .toList();
    }

    /**
     * Clear chat history.
     *
     * @param clientId the client id
     */
    public void clearHistory(String clientId) {
        ChatStore.clear(clientId);
    }
}
