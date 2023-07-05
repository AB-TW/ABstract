package cn.dpc.ab.domain.customer;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import lombok.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Customer represents a customer.
 */
@Getter
@RequiredArgsConstructor
public class Customer {
    private final CustomerId id;

    @Setter
    private FeatureConfigs featureConfigs;

    @Setter
    private Assignments assignments;

    /**
     * FeatureConfigs represents the feature configs of a customer.
     */
    public interface FeatureConfigs {
        Flux<FeatureConfig> getByFeatureKey(Feature.FeatureId featureKey);
    }

    /**
     * Assignments represents the assignments of a customer.
     */
    public interface Assignments {
        Mono<Assignment> getByExperimentId(Experiment.ExperimentId experimentId);
    }

    /**
     * CustomerId represents the id of a customer.
     */
    @Builder
    public record CustomerId(String customerId, String clientId, Map<String, Object> attributes) {}
}
