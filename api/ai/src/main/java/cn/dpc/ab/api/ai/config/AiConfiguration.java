package cn.dpc.ab.api.ai.config;

import org.springframework.ai.ollama.OllamaEmbeddingClient;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.vectorstore.SimpleVectorStore;
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
    public SimpleVectorStore simpleVectorStore(OpenAiEmbeddingClient embeddingClient) {
        return new SimpleVectorStore(embeddingClient);
    }

    @Bean
    public SimpleVectorStore historyVectorStore(OpenAiEmbeddingClient embeddingClient) {
        return new SimpleVectorStore(embeddingClient);
    }
}
