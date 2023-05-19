package cn.dpc.ab.domain.feature;

import lombok.Builder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Features provides an interface for managing features.
 */
public interface Features {


    /**
     * FilterParams provides a set of parameters to filter features.
     */
    @Builder
    record FilterParams(String key, Boolean enabled) {}

    /**
     * Get a feature.
     *
     * @param id the id of the feature to get
     * @return a Mono that completes when the feature has been retrieved
     */
    Mono<Feature> get(Feature.FeatureId id);

    /**
     * Get all features.
     *
     * @param params the filter params
     * @return a Flux that emits all features
     */
    Flux<Feature> getAll(FilterParams params);

    /**
     * Get a sub-collection of features.
     *
     * @param params the filter params
     * @param from   the start index of the sub-collection
     * @param to     the end index of the sub-collection
     * @return a Flux that emits a sub-collection of features
     */
    Flux<Feature> subCollection(FilterParams params, long from, long to);

    /**
     * Get the size of a sub-collection of features.
     *
     * @param params the filter params
     * @return a Mono that emits the size of a sub-collection of features
     */
    Mono<Long> size(FilterParams params);

    /**
     * Get all enabled features.
     *
     * @param params the filter params
     * @return a Flux that emits all enabled features
     */
    Flux<Feature> enabled(FilterParams params);


    /**
     * add a feature.
     *
     * @param feature the feature to add
     * @return a Mono that completes when the feature has been added
     */
    Mono<Feature> add(Feature feature);

    /**
     * Update a feature.
     *
     * @param id          the id of the feature to update
     * @param description the new description of the feature
     * @return a Mono that completes when the feature has been updated
     */
    Mono<Void> update(Feature.FeatureId id, Feature.Description description);

    /**
     * Enable a feature.
     *
     * @param id the id of the feature to enable
     * @return a Mono that completes when the feature has been enabled
     */
    Mono<Void> enable(Feature.FeatureId id);

    /**
     * Disable a feature.
     *
     * @param id the id of the feature to disable
     * @return a Mono that completes when the feature has been disabled
     */
    Mono<Void> disable(Feature.FeatureId id);
}
