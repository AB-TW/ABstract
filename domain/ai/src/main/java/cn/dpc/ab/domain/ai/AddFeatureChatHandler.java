package cn.dpc.ab.domain.ai;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.Features;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Add feature chat handler.
 */
public class AddFeatureChatHandler extends AbstractChatHandler {

    private final ObjectMapper objectMapper;
    private final Intention intention = Intention.ADD_FEATURE;
    private final Features features;

    /**
     * Instantiates a new Add feature chat handler.
     *
     * @param streamingChatClient the streaming chat client
     * @param chatClient          the chat client
     * @param objectMapper        the object mapper
     * @param features            the features
     */
    public AddFeatureChatHandler(StreamingChatModel streamingChatClient,
                                 ChatModel chatClient,
                                 ObjectMapper objectMapper,
                                 Features features) {
        super(streamingChatClient, chatClient);
        this.objectMapper = objectMapper;
        this.features = features;
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
        ChatResponse response = chatModel.call(new Prompt(messages));
        String content = response.getResult().getOutput().getContent();
        List<Slot> slots = Slot.extractSlotsFromContent(content, objectMapper);

        if (!slots.isEmpty()
                && slots.stream()
                .allMatch(slot -> !slot.isRequired()
                        || null != slot.getValue()
                        && !slot.getValue().isEmpty())) {

            FeatureRequest request = new FeatureRequest();
            slots.forEach(slot -> {
                if ("key".equalsIgnoreCase(slot.getTitle())) {
                    request.setFeatureKey(slot.getValue());
                }

                if ("name".equalsIgnoreCase(slot.getTitle())) {
                    request.setName(slot.getValue());
                }

                if ("description".equalsIgnoreCase(slot.getTitle())) {
                    request.setDescription(slot.getValue());
                }
            });

            return features.add(request.toFeature())
                    .flatMapMany(feature -> Flux.just("添加成功! key为" + feature.getId().featureKey() + "，可在Feature管理模块查看结果！"))
                    .onErrorResume(e -> {
                        List<Message> errorTipMessages = new ArrayList<>();
                        errorTipMessages.add(new SystemPromptTemplate("""
                                当前用户正在添加特性，但是添加特性失败，请根据如下error信息，给予用户简短友好的提示失败原因:
                                error: {error}
                                """)
                                .createMessage(Map.of("error",
                                        "{\"exceptionName\": \""
                                                + e.toString()
                                                + "\", \"message\": \""
                                                + e.getMessage() + "\"}")));
                        return streamChat(history, new Prompt(errorTipMessages), "");
                    }).doOnComplete(() -> ChatStore.restoreIntention(clientId));
        }

        ChatStore.addFeatureSlots.put(clientId, slots.isEmpty() ? addFeatureSlot : slots);

        SystemPromptTemplate queryPromptTemplate = new SystemPromptTemplate(intention.getQueryPrompt());
        Prompt queryprompt = new Prompt(List.of(queryPromptTemplate
                .createMessage(
                        Map.of("slots",
                                objectMapper.writeValueAsString(ChatStore.addFeatureSlots.get(clientId))))));

        return streamChat(history, queryprompt, "");
    }

    /**
     * The type Feature request.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FeatureRequest {
        private String featureKey;

        private String name;

        private String description;

        private boolean enabled;

        private Feature.FeatureTemplate template;


        /**
         * Creates a Feature from a FeatureRequest.
         *
         * @return the Feature
         */
        public Feature toFeature() {
            return new Feature(
                    new Feature.FeatureId(featureKey),
                    Feature.Description.builder()
                            .name(name)
                            .description(description)
                            .enabled(enabled)
                            .template(Optional.ofNullable(template).orElse(Feature.FeatureTemplate.DEFAULT))
                            .build());
        }
    }
}
