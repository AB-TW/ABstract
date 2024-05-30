package cn.dpc.ab.api.ai.bot;

import org.springframework.ai.chat.messages.Message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * State store of chatBot.
 */
public class ChatStore {

    public static final Map<String, List<Message>> histories = new ConcurrentHashMap<>();
    public static final Map<String, Intention> globalIntention = new ConcurrentHashMap<>();
    public static final Map<String, List<Slot>> addFeatureSlots = new ConcurrentHashMap<>();



    /**
     * Clear history of client.
     *
     * @param clientId the client id
     */
    public static void clear(String clientId) {
        histories.remove(clientId);
        globalIntention.remove(clientId);
        addFeatureSlots.remove(clientId);
    }

    /**
     * Restore Intention and Slots.
     *
     * @param clientId the client id
     */
    public static void restoreIntention(String clientId) {
        globalIntention.remove(clientId);
        addFeatureSlots.remove(clientId);
    }
}
