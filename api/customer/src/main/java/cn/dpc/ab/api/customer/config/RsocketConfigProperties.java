package cn.dpc.ab.api.customer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * RSocketConfigProperties.
 */
@Component
@ConfigurationProperties(prefix = "rsocket")
@Getter
@Setter
public class RsocketConfigProperties {
    /**
     * 是否开启FeatureConfig变更推送.
     */
    private boolean featureKeyPushEnabled;

    /**
     * 是否开启自定义WebSocket 协议端口.
     */
    private boolean customWebSocketEnabled;

    /**
     * 自定义WebSocket 协议端口.
     */
    private int customWebSocketPort = 7002;
}
