package cn.dpc.ab;

import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;

/**
 * Main application.
 */
@SpringBootApplication
public class MainApplication implements ApplicationRunner {

    @Autowired
    RSocketMessageHandler handler;

    /**
     * Main method.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        RSocketServer.create(handler.responder())
                .bind(WebsocketServerTransport.create("localhost", 7002))
                .block();
    }
}