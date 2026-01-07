package cn.dpc.ab.api.customer.config;

import cn.dpc.ab.domain.core.CustomerId;
import org.springframework.messaging.rsocket.RSocketRequester;

import java.util.HashMap;
import java.util.Map;

/**
 * RSocketRequesterStore.
 */
public class RsocketRequesterStore {
    public static final Map<String, Map<String, RSocketRequester>> requesters = new HashMap<>();

    /**
     * registerRequester.
     *
     * @param customerId             customerId
     * @param featureKey             key
     * @param requester              requester
     * @param rsocketConfigProperties rsocketConfigProperties
     */
    public static void registerRequester(CustomerId customerId, String featureKey,
                                         RSocketRequester requester,
                                         RsocketConfigProperties rsocketConfigProperties) {
        if (!rsocketConfigProperties.isFeatureKeyPushEnabled()) {
            return;
        }

        Map<String, RSocketRequester> customerRequesterMap = requesters.getOrDefault(customerId.clientId(), new HashMap<>());
        requesters.put(customerId.clientId(), customerRequesterMap);
        if (customerRequesterMap.get(featureKey) != requester) {
            customerRequesterMap.put(featureKey, requester);
        }
    }
}
