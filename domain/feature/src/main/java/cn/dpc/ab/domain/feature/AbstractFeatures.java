package cn.dpc.ab.domain.feature;

import cn.dpc.ab.domain.exception.RecordNotFoundException;
import reactor.core.publisher.Mono;

/**
 * AbstractFeatureFlags provides a skeleton implementation of the FeatureFlags interface.
 */
public abstract class AbstractFeatures implements Features {

    /**
     * Enable a feature flag.
     *
     * @param feature the feature flag to enable
     * @return a Mono that completes when the feature flag has been enabled
     */
    protected abstract Mono<Void> doEnable(Feature feature);


    /**
     * Enable a feature flag.
     *
     * @param id the id of the feature flag to enable
     * @return a Mono that completes when the feature flag has been enabled
     */
    public Mono<Void> enable(Feature.FeatureId id) {
        return this.get(id)
                .switchIfEmpty(Mono.error(new RecordNotFoundException(id.toString())))
                .flatMap(this::doEnable);
    }

    /**
     * Update a feature flag.
     *
     * @param feature the feature flag to update
     * @param description the new description of the feature flag
     * @return a Mono that completes when the feature flag has been updated
     */
    protected abstract Mono<Void> doUpdate(Feature feature, Feature.FeatureDescription description);

    /**
     * Update a feature flag.
     *
     * @param id          the id of the feature flag to update
     * @param description the new description of the feature flag
     * @return a Mono that completes when the feature flag has been updated
     */
    public Mono<Void> update(Feature.FeatureId id, Feature.FeatureDescription description) {
        return this.get(id)
                .switchIfEmpty(Mono.error(new RecordNotFoundException(id.toString())))
                .flatMap(feature -> doUpdate(feature, description));
    }

    /**
     * Disable a feature flag.
     *
     * @param feature the feature flag to disable
     * @return a Mono that completes when the feature flag has been disabled
     */
    protected abstract Mono<Void> doDisable(Feature feature);


    /**
     * Disable a feature flag.
     *
     * @param id the id of the feature flag to disable
     * @return a Mono that completes when the feature flag has been disabled
     */
    public Mono<Void> disable(Feature.FeatureId id) {
        return this.get(id)
                .switchIfEmpty(Mono.error(new RecordNotFoundException(id.toString())))
                .flatMap(this::doDisable);
    }
}
