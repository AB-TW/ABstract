package cn.dpc.ab.persistent.featrue;

import cn.dpc.ab.domain.feature.AbstractFeatures;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.Features;
import cn.dpc.ab.persistent.featrue.db.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * FeaturesImpl is the implementation of Features.
 */
@Component
@RequiredArgsConstructor
public class FeaturesImpl extends AbstractFeatures {
    private final FeatureRepository repository;
    private final CustomerFeatureRepository customerFeatureRepository;
    private final FeatureConfigRepository featureConfigRepository;
    private final CustomerFeatureConfigRepository customerFeatureConfigRepository;


    /**
     * enableToDb enables a feature in the database.
     *
     * @see AbstractFeatures#doEnable(Feature)
     */
    @Override
    public Mono<Void> doEnable(Feature feature) {
        return updateToDb(feature.getId().featureKey(), feature.enable().getDescription());
    }

    /**
     * doUpdate updates a feature in the database.
     *
     * @see AbstractFeatures#doUpdate(Feature,
     * Feature.Description)
     */
    @Override
    protected Mono<Void> doUpdate(Feature feature, Feature.Description description) {
        return updateToDb(feature.getId().featureKey(), feature.update(description).getDescription());
    }

    /**
     * doDisable disables a feature in the database.
     *
     * @see AbstractFeatures#doDisable(Feature)
     */
    @Override
    public Mono<Void> doDisable(Feature feature) {
        return updateToDb(feature.getId().featureKey(), feature.disable().getDescription());
    }

    /**
     * get retrieves a feature from the database.
     *
     * @see Features#get(Feature.FeatureId)
     */
    @Override
    public Mono<Feature> get(Feature.FeatureId id) {
        return repository.findByFeatureKey(id.featureKey())
                .map(FeatureDb::to)
                .map(feature -> {
                    feature.setConfigs(new FeatureConfigsImpl(featureConfigRepository, customerFeatureConfigRepository, feature.getId()));
                    return feature;
                });
    }

    /**
     * getAll retrieves all features from the database.
     *
     * @see Features#getAll(Features.FilterParams)
     */
    @Override
    public Flux<Feature> getAll(Features.FilterParams params) {
        return customerFeatureRepository.findAll(params)
                .map(FeatureDb::to);
    }

    /**
     * subCollection retrieves a sub-collection of features from the database
     * that match the given filter.
     *
     * @see Features#subCollection(Features.FilterParams,
     * long, long)
     */
    @Override
    public Flux<Feature> subCollection(Features.FilterParams params, long from, long to) {
        int pageSize = (int) (to - from);
        pageSize = pageSize < 0 ? 10 : pageSize;
        int page = (int) (from / pageSize);
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by("description.updatedAt").descending());

        return customerFeatureRepository.findAll(params, pageRequest)
                .map(FeatureDb::to);
    }

    /**
     * size retrieves the number of features from the database that match the
     * given filter.
     *
     * @see Features#size(Features.FilterParams)
     */
    @Override
    public Mono<Long> size(Features.FilterParams params) {
        return customerFeatureRepository.count(params);
    }

    /**
     * enabled retrieves all enabled features from the database.
     *
     * @see Features#enabled(Features.FilterParams)
     */
    @Override
    public Flux<Feature> enabled(Features.FilterParams params) {
        return getAll(new Features.FilterParams(params.key(), true));
    }

    /**
     * disabled retrieves all disabled features from the database.
     *
     * @see Features#add(Feature)
     */
    @Override
    public Mono<Feature> add(Feature feature) {
        return repository.save(FeatureDb.from(feature)).map(FeatureDb::to);
    }

    /**
     * updateToDb updates a feature in the database.
     *
     * @param featureKey  the feature key
     * @param description the feature description
     * @return a Mono of Void
     */
    private Mono<Void> updateToDb(String featureKey, Feature.Description description) {
        return repository.findByFeatureKey(featureKey)
                .flatMap(db -> {
                    db.updateDescription(description);
                    return repository.save(db);
                }).then();
    }
}
