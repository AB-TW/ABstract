package cn.dpc.ab.persistent.ai;

import cn.dpc.ab.domain.ai.ChatBot;
import cn.dpc.ab.domain.feature.Features;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.Set;

/**
 * The type Chatbot configuration.
 */
@Configuration
public class ChatbotConfiguration {

    /**
     * Chat bot chat bot.
     *
     * @param systemResource      the system resource
     * @param openAiChatModel     the open ai chat model
     * @param qaVectorStore       the qa vector store
     * @param objectMapper         the object mapper
     * @param historyVectorStore  the history vector store
     * @param features            the features
     * @param functions           the functions
     * @param enabled             the enabled
     * @return the chat bot
     */
    @Bean
    public ChatBot chatBot(@Value("classpath:/prompts/system-message.st")
                           Resource systemResource,
                           OpenAiChatModel openAiChatModel,
                           @Qualifier("simpleVectorStore")
                           SimpleVectorStore qaVectorStore,
                           ObjectMapper objectMapper,
                           @Qualifier("historyVectorStore")
                           SimpleVectorStore historyVectorStore,
                           Features features,
                           @Qualifier("functions")
                           Set<String> functions,
                           @Value("${ai.enabled}")
                           boolean enabled) {
        return new ChatBot(systemResource, openAiChatModel, openAiChatModel,
                qaVectorStore, objectMapper, historyVectorStore,
                features, OpenAiChatOptions.builder()
                .withFunctions(functions)
                .build(), enabled);
    }
}
