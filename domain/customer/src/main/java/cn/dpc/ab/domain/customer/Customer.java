package cn.dpc.ab.domain.customer;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Customer represents a customer.
 */
@Builder
@Getter
@AllArgsConstructor
public class Customer {
    private String id;

    private String clientId;

    private Map<String, Object> attributes;

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
}
