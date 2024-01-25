package cn.dpc.ab.api.customer.config;

import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.stereotype.Component;

/**
 * CustomWebsocketRSocketServerConfig.
 */
@Component
@RequiredArgsConstructor
public class CustomWebsocketRsocketServerConfig implements ApplicationRunner {
    private final RSocketMessageHandler handler;
    private final RsocketConfigProperties rsocketConfigProperties;

    @Override
    public void run(ApplicationArguments args) {
        if (!rsocketConfigProperties.isCustomWebSocketEnabled()) {
            return;
        }

        RSocketServer.create(handler.responder())
                .bind(WebsocketServerTransport.create(rsocketConfigProperties.getCustomWebSocketPort()))
                .block();
    }
}
