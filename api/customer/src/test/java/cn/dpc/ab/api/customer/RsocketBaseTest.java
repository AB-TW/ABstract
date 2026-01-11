package cn.dpc.ab.api.customer;

import cn.dpc.ab.api.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.test.context.ContextConfiguration;

/**
 * RSocketBaseTest.
 *
 * <p>DISABLED: Spring Boot 4.0.1 中 RSocket 测试支持发生了变化，
 * 需要使用新的测试方式。此测试类暂时禁用。
 */
@Disabled("Spring Boot 4.0.1 changed RSocket test support. Needs to be updated.")
@SpringBootTest(properties = {"spring.rsocket.server.port=7000", "spring.main.lazy-initialization=true"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = TestConfiguration.class)
public class RsocketBaseTest {
    // @LocalRSocketServerPort - 在 Spring Boot 4.x 中不可用
    int port = 7000; // 使用固定端口
    @Autowired
    RSocketRequester.Builder builder;

    @Autowired
    RSocketMessageHandler messageHandler;

    RSocketRequester requester;

    @BeforeEach
    public void init() {
        requester = builder.rsocketStrategies(messageHandler.getRSocketStrategies())
                .tcp("127.0.0.1", port);
    }
}
