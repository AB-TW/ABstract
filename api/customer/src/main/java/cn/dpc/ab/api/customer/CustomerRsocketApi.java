package cn.dpc.ab.api.customer;

import cn.dpc.ab.api.customer.config.RsocketConfigProperties;
import cn.dpc.ab.api.customer.config.RsocketRequesterStore;
import cn.dpc.ab.api.customer.dto.FeatureConfigResponse;
import cn.dpc.ab.domain.core.CustomerId;
import cn.dpc.ab.domain.customer.Customers;
import cn.dpc.ab.domain.feature.Feature;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import static cn.dpc.ab.api.customer.config.ServerConfig.CUSTOMER;

/**
 * CustomerRsocketApi represents the RSocket API for customers.
 */
@Controller
@RequiredArgsConstructor
public class CustomerRsocketApi {
    private final Customers customers;
    private final RsocketConfigProperties rsocketConfigProperties;

    @MessageMapping("customers.featureConfigs.{featureKey}")
    Flux<FeatureConfigResponse> getFeatureConfigs(@DestinationVariable("featureKey") String featureKey,
                                                  @Header(CUSTOMER) CustomerId customerId,
                                                  RSocketRequester requester) {
        RsocketRequesterStore.registerRequester(customerId, featureKey, requester, rsocketConfigProperties);

        return customers.getById(customerId)
                .flatMapMany(customer -> Flux.concat(
                        customer.getAssignments()
                                .getByFeatureKey(Feature.FeatureId.builder().featureKey(featureKey).build())
                                .map(assignment -> FeatureConfigResponse.from(assignment, featureKey)),
                        customer.getFeatureConfigs()
                                .getByFeatureKey(Feature.FeatureId.builder().featureKey(featureKey).build())
                                .map(FeatureConfigResponse::from))
                ).filter(featureConfigResponse -> featureConfigResponse != FeatureConfigResponse.EMPTY);
    }

    @MessageMapping("customers.featureConfigs")
    Flux<FeatureConfigResponse> getMultiFeatureConfigs(@Header(CUSTOMER) CustomerId customerId,
                                                       @Payload Flux<String> featureKeys) {
        return customers.getById(customerId)
                .flatMapMany(customer ->
                        featureKeys.flatMap(featureKey ->
                                Flux.concat(
                                        customer.getAssignments()
                                                .getByFeatureKey(Feature.FeatureId.builder().featureKey(featureKey).build())
                                                .map(assignment -> FeatureConfigResponse.from(assignment, featureKey)),
                                        customer.getFeatureConfigs()
                                                .getByFeatureKey(Feature.FeatureId.builder().featureKey(featureKey).build())
                                                .map(FeatureConfigResponse::from))
                        ).filter(featureConfigResponse -> featureConfigResponse != FeatureConfigResponse.EMPTY)
                );
    }

}
