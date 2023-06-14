package cn.dpc.ab.persistent.feature;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.persistent.feature.db.FeatureConfigDb;
import cn.dpc.ab.persistent.feature.db.FeatureConfigDbRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

/**
 * FeatureConfigsImpl implements FeatureConfigs.
 */
@RequiredArgsConstructor
public class FeatureConfigsImpl implements Feature.Configs {
    private final FeatureConfigDbRepository repository;
    private final Feature.FeatureId featureId;

    @Override
    public Flux<FeatureConfig> getAll(FilterParams params) {
        return repository.findByFeatureKey(featureId.featureKey())
                .map(FeatureConfigDb::toFeatureConfig)
                .filter(featureConfig -> (params.title() == null
                        || Optional.ofNullable(featureConfig.getDescription().description())
                        .map(String::toLowerCase)
                        .map(description -> description.contains(params.title()))
                        .orElse(false)
                        || Optional.ofNullable(featureConfig.getDescription().title())
                        .map(String::toLowerCase)
                        .map(description -> description.contains(params.title()))
                        .orElse(false))
                        && Optional.ofNullable(params.status())
                        .map(status -> featureConfig.getDescription().status().getStatus() == status)
                        .orElse(true))
                .sort(new FeatureConfigComparator());
    }

    @Override
    public Flux<FeatureConfig> subCollection(FilterParams params, long from, long to) {
        return getAll(params).skip(from).take(to - from);
    }

    @Override
    public Mono<Long> size(FilterParams params) {
        return getAll(params).count();
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

    private static class FeatureConfigComparator implements Comparator<FeatureConfig> {

        @Override
        public int compare(FeatureConfig o1, FeatureConfig o2) {
            // compare updatedAt reversed
            Optional<FeatureConfig.Description> descriptionO1 = Optional.ofNullable(o1.getDescription());
            var updatedAtO1 = descriptionO1
                    .map(FeatureConfig.Description::updatedAt)
                    .orElse(LocalDateTime.MIN);

            Optional<FeatureConfig.Description> descriptionO2 = Optional.ofNullable(o2.getDescription());
            var updatedAtO2 = descriptionO2
                    .map(FeatureConfig.Description::updatedAt)
                    .orElse(LocalDateTime.MIN);

            if (updatedAtO2.compareTo(updatedAtO1) != 0) {
                return updatedAtO2.compareTo(updatedAtO1);
            }

            // then compare createdAt reversed
            var createdAtO1 = descriptionO1
                    .map(FeatureConfig.Description::createdAt)
                    .orElse(LocalDateTime.MIN);
            var createdAtO2 = descriptionO2
                    .map(FeatureConfig.Description::createdAt)
                    .orElse(LocalDateTime.MIN);

            if (createdAtO2.compareTo(createdAtO1) != 0) {
                return createdAtO2.compareTo(createdAtO1);
            }

            // then compare priority
            return (int) (o1.getDescription().priority() - o2.getDescription().priority());
        }
    }
}

