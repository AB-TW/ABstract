package cn.dpc.ab.persistent.ai;

import cn.dpc.ab.domain.ai.AiProperties;
import cn.dpc.ab.domain.ai.*;
import cn.dpc.ab.domain.feature.Features;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.Map;
import java.util.Set;

/**
 * The type Chatbot configuration.
 */
@Configuration
public class ChatbotConfiguration {

    /**
     * Chat bot chat bot.
     *
     * @param systemResource     the system resource
     * @param openAiChatModel    the open ai chat model
     * @param qaVectorStore      the qa vector store
     * @param objectMapper       the object mapper
     * @param historyVectorStore the history vector store
     * @param features           the features
     * @param functions          the functions
     * @param aiProperties       ai properties
     * @return the chat bot
     */
    @Bean
    public ChatBot chatBot(@Value("classpath:/prompts/system-message.st")
                           Resource systemResource,
                           OllamaChatModel openAiChatModel,
                           @Qualifier("simpleVectorStore")
                           SimpleVectorStore qaVectorStore,
                           ObjectMapper objectMapper,
                           @Qualifier("historyVectorStore")
                           SimpleVectorStore historyVectorStore,
                           Features features,
                           @Qualifier("functions")
                           Set<String> functions,
                           AiProperties aiProperties) {
        var qaChatHandler = new QueryChatHandler(openAiChatModel, openAiChatModel, qaVectorStore, systemResource,
                OllamaOptions.create(), objectMapper, aiProperties);
        var addFeatureChatHandler = new AddFeatureChatHandler(openAiChatModel, openAiChatModel, objectMapper, features);
        var queryWithFunctionChatHandler = new QueryWithFunctionChatHandler(openAiChatModel, openAiChatModel,
                OllamaOptions.builder().withFunctions(functions).build());
        var intentionHandler = new IntentionHandler(objectMapper, ChatClient.builder(openAiChatModel).build(), aiProperties);
        return new ChatBot(historyVectorStore,
                Map.of(Intention.QA, qaChatHandler,
                        Intention.ADD_FEATURE, addFeatureChatHandler,
                        Intention.QUERY_WITH_FUNCTION, queryWithFunctionChatHandler),
                aiProperties.isEnabled(),
                intentionHandler);
    }
}
