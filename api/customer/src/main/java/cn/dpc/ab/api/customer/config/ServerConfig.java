package cn.dpc.ab.api.customer.config;

import cn.dpc.ab.domain.core.CustomerId;
import io.rsocket.core.Resume;
import io.rsocket.lease.Lease;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * ServerConfig.
 */
@Configuration
public class ServerConfig {

    public static final String CUSTOMER = "customer";

    /**
     * RSocketStrategiesCustomizer.
     *
     * @return RSocketStrategiesCustomizer
     */
    @Bean
    public RSocketStrategiesCustomizer strategiesCustomizer() {
        return strategies ->
                strategies.metadataExtractorRegistry(registry -> registry.metadataToExtract(MediaType.APPLICATION_JSON,
                        CustomerId.class, CUSTOMER));
    }

    /**
     * RSocketServerCustomizer.
     *
     * @return RSocketServerCustomizer
     */
    @Bean
    public RSocketServerCustomizer serverCustomizer() {
        return rsocketServer -> {
            rsocketServer.resume(new Resume());
        };
    }
}
