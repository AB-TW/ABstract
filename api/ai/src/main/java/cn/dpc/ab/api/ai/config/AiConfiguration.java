package cn.dpc.ab.api.ai.config;

import lombok.Getter;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration for AI module.
 */
@Configuration
@ConfigurationProperties("ai.enabled")
@Getter
public class AiConfiguration {

    private boolean enabled;

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public SimpleVectorStore simpleVectorStore(OpenAiEmbeddingClient embeddingClient) {
        return new SimpleVectorStore(embeddingClient);
    }

    @Bean
    public SimpleVectorStore historyVectorStore(OpenAiEmbeddingClient embeddingClient) {
        return new SimpleVectorStore(embeddingClient);
    }
}
