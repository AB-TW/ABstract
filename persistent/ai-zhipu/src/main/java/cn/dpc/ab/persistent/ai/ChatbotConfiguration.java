package cn.dpc.ab.persistent.ai;

import cn.dpc.ab.domain.ai.*;
import cn.dpc.ab.domain.feature.Features;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
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
     * @param zhiPuAiChatModel   the ZhiPu ai chat model
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
                           ZhiPuAiChatModel zhiPuAiChatModel,
                           @Qualifier("simpleVectorStore")
                           SimpleVectorStore qaVectorStore,
                           ObjectMapper objectMapper,
                           @Qualifier("historyVectorStore")
                           SimpleVectorStore historyVectorStore,
                           Features features,
                           @Qualifier("functions")
                           Set<String> functions,
                           AiProperties aiProperties) {
        var qaChatHandler = new QueryChatHandler(zhiPuAiChatModel, zhiPuAiChatModel, qaVectorStore, systemResource,
                ZhiPuAiChatOptions.builder()
                        .build(), objectMapper, aiProperties);
        var addFeatureChatHandler = new AddFeatureChatHandler(zhiPuAiChatModel, zhiPuAiChatModel, objectMapper, features);
        var queryWithFunctionChatHandler = new QueryWithFunctionChatHandler(zhiPuAiChatModel, zhiPuAiChatModel, ZhiPuAiChatOptions.builder()
                .functions(functions)
                .build());
        var intentionHandler = new IntentionHandler(objectMapper, ChatClient.builder(zhiPuAiChatModel).build(), aiProperties);
        return new ChatBot(historyVectorStore,
                Map.of(Intention.QA, qaChatHandler,
                        Intention.ADD_FEATURE, addFeatureChatHandler,
                        Intention.QUERY_WITH_FUNCTION, queryWithFunctionChatHandler),
                aiProperties.isEnabled(),
                intentionHandler);
    }
}
