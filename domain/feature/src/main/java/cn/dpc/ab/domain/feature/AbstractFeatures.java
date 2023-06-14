package cn.dpc.ab.domain.feature;

import reactor.core.publisher.Mono;

/**
 * AbstractFeatures provides a skeleton implementation of the Features interface.
 */
public abstract class AbstractFeatures implements Features {

    /**
     * Enable a feature.
     *
     * @param feature the feature to enable
     * @return a Mono that completes when the feature has been enabled
     */
    protected abstract Mono<Void> doEnable(Feature feature);


    /**
     * Enable a feature.
     *
     * @param id the id of the feature to enable
     * @return a Mono that completes when the feature has been enabled
     */
    public Mono<Void> enable(Feature.FeatureId id) {
        return this.get(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Feature not found: " + id.toString())))
                .flatMap(this::doEnable);
    }

    /**
     * Update a feature.
     *
     * @param feature the feature to update
     * @param description the new description of the feature
     * @return a Mono that completes when the feature has been updated
     */
    protected abstract Mono<Void> doUpdate(Feature feature, Feature.Description description);

    /**
     * Update a feature.
     *
     * @param id          the id of the feature to update
     * @param description the new description of the feature
     * @return a Mono that completes when the feature has been updated
     */
    public Mono<Void> update(Feature.FeatureId id, Feature.Description description) {
        return this.get(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Feature not found: " + id.toString())))
                .flatMap(feature -> doUpdate(feature, description));
    }

    /**
     * Disable a feature.
     *
     * @param feature the feature to disable
     * @return a Mono that completes when the feature has been disabled
     */
    protected abstract Mono<Void> doDisable(Feature feature);


    /**
     * Disable a feature.
     *
     * @param id the id of the feature to disable
     * @return a Mono that completes when the feature has been disabled
     */
    public Mono<Void> disable(Feature.FeatureId id) {
        return this.get(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Feature not found: " + id.toString())))
                .flatMap(this::doDisable);
    }
}
