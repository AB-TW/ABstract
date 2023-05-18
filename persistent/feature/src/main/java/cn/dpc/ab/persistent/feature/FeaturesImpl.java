package cn.dpc.ab.persistent.feature;

import cn.dpc.ab.domain.feature.AbstractFeatures;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.Feature.FeatureDescription;
import cn.dpc.ab.domain.feature.Features;
import cn.dpc.ab.persistent.feature.db.FeatureDb;
import cn.dpc.ab.persistent.feature.db.FeatureFlagDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * FeatureFlagsImpl provides a skeleton implementation of the FeatureFlags interface.
 */
@Component
@RequiredArgsConstructor
public class FeaturesImpl extends AbstractFeatures {
    private final FeatureFlagDbRepository repository;

    /**
     * enableToDb enables a feature flag in the database.
     *
     * @see AbstractFeatures#doEnable(Feature)
     */
    @Override
    public Mono<Void> doEnable(Feature feature) {
        return updateToDb(feature.getId().featureKey(), feature.enable().getDescription());
    }

    /**
     * doUpdate updates a feature flag in the database.
     *
     * @see AbstractFeatures#doUpdate(Feature,
     * FeatureDescription)
     */
    @Override
    protected Mono<Void> doUpdate(Feature feature, FeatureDescription description) {
        return updateToDb(feature.getId().featureKey(), feature.update(description).getDescription());
    }

    /**
     * doDisable disables a feature flag in the database.
     *
     * @see AbstractFeatures#doDisable(Feature)
     */
    @Override
    public Mono<Void> doDisable(Feature feature) {
        return updateToDb(feature.getId().featureKey(), feature.disable().getDescription());
    }

    /**
     * get retrieves a feature flag from the database.
     *
     * @see Features#get(Feature.FeatureId)
     */
    @Override
    public Mono<Feature> get(Feature.FeatureId id) {
        return repository.findById(FeatureDb.idFromFeatureKey(id.featureKey()))
                .map(FeatureDb::to);
    }

    /**
     * getAll retrieves all feature flags from the database.
     *
     * @see Features#getAll(Features.FilterParams)
     */
    @Override
    public Flux<Feature> getAll(FilterParams params) {
        return getAllByFilter(params)
                .map(FeatureDb::to);
    }

    /**
     * getAllByFilter retrieves all feature flags from the database that match the
     * given filter.
     *
     * @param params the filter parameters
     * @return a Flux of FeatureFlagDb
     */
    private Flux<FeatureDb> getAllByFilter(FilterParams params) {
        return repository.findAll()
                .filter(db -> (params.key() == null || db.getFeatureKey().contains(params.key()))
                        && (params.enabled() == null || db.getDescription().enabled() == params.enabled())
                );
    }

    /**
     * subCollection retrieves a sub-collection of feature flags from the database
     * that match the given filter.
     *
     * @see Features#subCollection(Features.FilterParams,
     * long, long)
     */
    @Override
    public Flux<Feature> subCollection(FilterParams params, long from, long to) {
        return this.getAll(params).skip(from).take(to - from);
    }

    /**
     * size retrieves the number of feature flags from the database that match the
     * given filter.
     *
     * @see Features#size(Features.FilterParams)
     */
    @Override
    public Mono<Long> size(FilterParams params) {
        return getAllByFilter(params).count();
    }

    /**
     * enabled retrieves all enabled feature flags from the database.
     *
     * @see Features#enabled(Features.FilterParams)
     */
    @Override
    public Flux<Feature> enabled(FilterParams params) {
        return getAll(new FilterParams(params.key(), true));
    }

    /**
     * disabled retrieves all disabled feature flags from the database.
     *
     * @see Features#add(Feature)
     */
    @Override
    public Mono<Feature> add(Feature feature) {
        return repository.save(FeatureDb.from(feature)).map(FeatureDb::to);
    }

    /**
     * updateToDb updates a feature flag in the database.
     *
     * @param featureKey  the feature key
     * @param description the feature flag description
     * @return a Mono of Void
     */
    private Mono<Void> updateToDb(String featureKey, FeatureDescription description) {
        return repository.findById(FeatureDb.idFromFeatureKey(featureKey))
                .flatMap(db -> {
                    db.setDescription(description);
                    return repository.save(db);
                }).then();
    }
}
