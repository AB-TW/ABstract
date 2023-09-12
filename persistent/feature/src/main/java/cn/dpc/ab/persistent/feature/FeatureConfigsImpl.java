package cn.dpc.ab.persistent.feature;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.persistent.feature.db.FeatureConfigDb;
import cn.dpc.ab.persistent.feature.db.FeatureConfigDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * FeatureConfigsImpl implements FeatureConfigs.
 */
@RequiredArgsConstructor
public class FeatureConfigsImpl implements Feature.Configs {
    private final FeatureConfigDbRepository repository;
    private final Feature.FeatureId featureId;

    @Override
    public Flux<FeatureConfig> subCollection(FilterParams params, long from, long to) {
        int pageSize = (int) (to - from);
        int page = (int) (from / pageSize);
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by("description.updatedAt").descending()
                .and(Sort.by("description.createdAt").descending()));
        return repository.findByFilterParams(featureId.featureKey(), FeatureConfigDbRepository.FilterParams.from(params), pageRequest)
                .map(FeatureConfigDb::toFeatureConfig);
    }

    @Override
    public Mono<Long> size(FilterParams params) {
        return repository.countByFilterParams(featureId.featureKey(), FeatureConfigDbRepository.FilterParams.from(params));
    }

    @Override
    public Mono<FeatureConfig> get(FeatureConfig.FeatureConfigId id) {
        return repository.findById(id.configId())
                .map(FeatureConfigDb::toFeatureConfig);
    }

    @Override
    public Mono<FeatureConfig> add(FeatureConfig featureConfig) {
        return repository.save(FeatureConfigDb.from(featureConfig))
                .map(FeatureConfigDb::toFeatureConfig);
    }

    @Override
    public Mono<Void> update(FeatureConfig.FeatureConfigId id, FeatureConfig.Description description) {
        return repository.findById(id.configId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("FeatureConfig not found.")))
                .flatMap(featureConfigDB -> {
                    featureConfigDB.updateDescription(featureConfigDB.toFeatureConfig().update(description).getDescription());
                    return repository.save(featureConfigDB);
                }).then();
    }

    @Override
    public Mono<Void> delete(FeatureConfig.FeatureConfigId id) {
        return repository.findById(id.configId())
                .flatMap(featureConfigDB -> {
                    if (featureConfigDB.getDescription().getStatus().getStatus() == FeatureConfig.Status.DynamicStatus.DRAFT) {
                        return repository.delete(featureConfigDB);
                    }
                    featureConfigDB.updateDescription(featureConfigDB.toFeatureConfig().delete().getDescription());
                    return repository.save(featureConfigDB)
                            .then();
                });
    }

    @Override
    public Mono<Void> publish(FeatureConfig.FeatureConfigId id) {
        return repository.findById(id.configId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("FeatureConfig not found.")))
                .flatMap(featureConfigDB -> {
                    featureConfigDB.updateDescription(featureConfigDB.toFeatureConfig().publish().getDescription());
                    return repository.save(featureConfigDB);
                }).then();
    }

    @Override
    public Mono<Void> disable(FeatureConfig.FeatureConfigId id) {
        return repository.findById(id.configId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("FeatureConfig not found.")))
                .flatMap(featureConfigDB -> {
                    featureConfigDB.updateDescription(featureConfigDB.toFeatureConfig().disable().getDescription());
                    return repository.save(featureConfigDB);
                }).then();
    }
}

