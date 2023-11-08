package cn.dpc.ab.api.customer;

import cn.dpc.ab.api.customer.dto.FeatureConfigResponse;
import cn.dpc.ab.domain.core.CustomerId;
import cn.dpc.ab.domain.customer.Customers;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * CustomerApi represents the API for customers.
 */
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerApi {
    private final Customers customers;

    @PostMapping("/featureConfigs/{featureKey}")
    Flux<FeatureConfigResponse> getFeatureConfigs(@PathVariable("featureKey") String featureKey,
                                                  @RequestBody CustomerId customerId) {
        return customers.getById(customerId)
                .flatMapMany(customer ->
                        Flux.concat(customer.getAssignments()
                                .getByFeatureKey(Feature.FeatureId.builder().featureKey(featureKey).build())
                                .map(assignment -> FeatureConfigResponse.from(
                                        (FeatureConfig) assignment.getDescription().bucket().config())),
                                        customer.getFeatureConfigs()
                                                .getByFeatureKey(Feature.FeatureId.builder().featureKey(featureKey).build())
                                                .map(FeatureConfigResponse::from))
                );
    }

}
