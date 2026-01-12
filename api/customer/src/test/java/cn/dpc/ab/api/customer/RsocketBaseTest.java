package cn.dpc.ab.api.customer;

import cn.dpc.ab.api.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.server.RSocketServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.test.context.ContextConfiguration;

/**
 * RSocketBaseTest.
 *
 * <p>使用随机端口进行 RSocket 测试，避免端口冲突。
 */
@SpringBootTest(properties = {
        "spring.rsocket.server.port=0",
        "spring.main.lazy-initialization=true"
},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = {TestConfiguration.class})
public class RsocketBaseTest {
    @Autowired
    RSocketRequester.Builder builder;

    @Autowired
    RSocketMessageHandler messageHandler;

    @Autowired(required = false)
    RSocketServer rsocketServer;

    RSocketRequester requester;
    int port;

    /**
     * Initialize RSocket requester with server port.
     */
    @BeforeEach
    public void init() {
        // 获取服务器实际绑定的端口
        if (rsocketServer != null && rsocketServer.address() != null) {
            port = rsocketServer.address().getPort();
        } else {
            // 备用方案：使用环境变量或默认值
            String portStr = System.getProperty("rsocket.server.port", "0");
            port = portStr.equals("0") ? 7000 : Integer.parseInt(portStr);
        }

        requester = builder.rsocketStrategies(messageHandler.getRSocketStrategies())
                .tcp("127.0.0.1", port);
    }
}
