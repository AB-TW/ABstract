package cn.dpc.ab.domain.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.dpc.ab.domain.ai.Intention.*;


/**
 * The type Chat bot.
 */
public class ChatBot {
    private final SimpleVectorStore historyVectorStore;

    private final boolean enabled;
    private final Map<Intention, ChatHandler> chatHandlerMap;
    private final OtherChatHandler otherChatHandler = new OtherChatHandler();

    /**
     * Instantiates a new cn.dpc.ab.domain.ai.ChatBot.
     *
     * @param historyVectorStore the history vector store
     * @param chatHandlerMap     the chat handler map
     * @param enabled            the enabled
     */
    public ChatBot(
            SimpleVectorStore historyVectorStore,
            Map<Intention, ChatHandler> chatHandlerMap,
            boolean enabled) {
        this.historyVectorStore = historyVectorStore;
        this.chatHandlerMap = chatHandlerMap;
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
        List<Message> history = getHistoryOfClient(clientId);
        if (!this.enabled) {
            return otherChatHandler.handle(clientId, message, history);
        }

        var intention = Intention.getIntentionByMessage(message, historyVectorStore);
        if (intention != OTHER && intention != QA && intention != QUERY_FEATURE) {
            ChatStore.globalIntention.put(clientId, intention);
        }

        if (intention == OTHER) {
            intention = ChatStore.globalIntention.getOrDefault(clientId, OTHER);
        }

        return chatHandlerMap.containsKey(intention)
                ? chatHandlerMap.get(intention).handle(clientId, message, history)
                : otherChatHandler.handle(clientId, message, history);
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
