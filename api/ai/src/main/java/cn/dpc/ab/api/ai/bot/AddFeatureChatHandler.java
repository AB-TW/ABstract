package cn.dpc.ab.api.ai.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Add feature chat handler.
 */
public class AddFeatureChatHandler extends AbstractChatHandler {

    private final ObjectMapper objectMapper;
    private final Intention intention = Intention.ADD_FEATURE;

    /**
     * Instantiates a new Add feature chat handler.
     *
     * @param streamingChatClient the streaming chat client
     * @param chatClient          the chat client
     * @param objectMapper        the object mapper
     */
    public AddFeatureChatHandler(StreamingChatClient streamingChatClient,
                                 ChatClient chatClient,
                                 ObjectMapper objectMapper) {
        super(streamingChatClient, chatClient);
        this.objectMapper = objectMapper;
    }

    @Override
    public Flux<String> handle(String clientId, String message, List<Message> history) throws JsonProcessingException {
        List<Slot> addFeatureSlot = ChatStore.addFeatureSlots.getOrDefault(clientId, new ArrayList<>());
        if (addFeatureSlot.isEmpty()) {
            addFeatureSlot = intention.getSlots();
            ChatStore.addFeatureSlots.put(clientId, addFeatureSlot);
        }

        SystemPromptTemplate promptTemplate = new SystemPromptTemplate(intention.getUpdatePrompt());
        UserMessage userMessage = new UserMessage(message);
        List<Message> messages = new ArrayList<>();
        messages.add(promptTemplate.createMessage(Map.of("slots", objectMapper.writeValueAsString(addFeatureSlot))));
        if (!history.isEmpty()) {
            messages.add(history.get(history.size() - 1));
        }
        messages.add(userMessage);

        history.add(userMessage);
        ChatResponse response = chatClient.call(new Prompt(messages));
        String content = response.getResult().getOutput().getContent();
        List<Slot> slots = Slot.extractSlotsFromContent(content, objectMapper);

        if (!slots.isEmpty()
                && slots.stream()
                .allMatch(slot -> !slot.isRequired()
                        || null != slot.getValue()
                        && !slot.getValue().isEmpty())) {

            var prompt = new Prompt(List.of(new UserMessage("请帮我创建特性: "
                    + slots.stream()
                    .map(slot -> slot.getTitle() + ":"
                            + slot.getValue()).collect(Collectors.joining("  ")))),
                    OpenAiChatOptions.builder()
                            .withFunctions(Set.of("addFeature"))
                            .build());
            history.add(userMessage);
            return streamChat(history, prompt, "")
                    .doOnComplete(() -> ChatStore.restoreIntention(clientId));
        }

        ChatStore.addFeatureSlots.put(clientId, slots.isEmpty() ? addFeatureSlot : slots);

        SystemPromptTemplate queryPromptTemplate = new SystemPromptTemplate(intention.getQueryPrompt());
        Prompt queryprompt = new Prompt(List.of(queryPromptTemplate.createMessage(Map.of()),
                new UserMessage(objectMapper.writeValueAsString(ChatStore.addFeatureSlots.get(clientId)))));

        return streamChat(history, queryprompt, "");
    }
}
