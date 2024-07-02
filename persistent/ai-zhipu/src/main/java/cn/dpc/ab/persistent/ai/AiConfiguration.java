package cn.dpc.ab.persistent.ai;

import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration for AI module.
 */
@Configuration
public class AiConfiguration {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public SimpleVectorStore simpleVectorStore(ZhiPuAiEmbeddingModel embeddingModel) {
        return new SimpleVectorStore(embeddingModel);
    }

    @Bean
    public SimpleVectorStore historyVectorStore(ZhiPuAiEmbeddingModel embeddingModel) {
        return new SimpleVectorStore(embeddingModel);
    }
}
