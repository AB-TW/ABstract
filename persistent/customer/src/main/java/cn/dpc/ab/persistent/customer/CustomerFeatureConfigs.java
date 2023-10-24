package cn.dpc.ab.persistent.customer;

import cn.dpc.ab.domain.core.CustomerId;
import cn.dpc.ab.domain.customer.Customer;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.domain.feature.Features;
import cn.dpc.ab.persistent.cache.LocalCacheManager;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

/**
 * CustomerFeatureConfigs implements Customer.FeatureConfigs.
 */
@RequiredArgsConstructor
public class CustomerFeatureConfigs implements Customer.FeatureConfigs {
    private final Features features;
    private final CustomerId customerId;
    private final LocalCacheManager localCacheManager;


    @Override
    public Flux<FeatureConfig> getByFeatureKey(Feature.FeatureId featureKey) {
        // TODO: add cache

        return getByFeatureKeyFromCache(featureKey)
                .filter(featureConfig -> Optional.ofNullable(featureConfig.getDescription())
                        .map(description -> description.status().getStatus() == FeatureConfig.Status.DynamicStatus.RUNNING)
                        .orElse(false))
                .filter(featureConfig -> Optional.ofNullable(featureConfig.getDescription().filterCondition())
                        .map(filterCondition -> filterCondition.match(customerId))
                        .orElse(true))
                .sort(new FeatureConfigComparator());
    }

    private Flux<FeatureConfig> getByFeatureKeyFromCache(Feature.FeatureId featureKey) {
        Feature.Configs.FilterParams params = Feature.Configs.FilterParams.builder()
                .status(FeatureConfig.Status.DynamicStatus.RUNNING)
                .build();

        return (Flux<FeatureConfig>) localCacheManager.cacheFlux(featureKey.featureKey(),
                () -> features.get(featureKey)
                        .flatMapMany(feature -> feature.getConfigs().all(params)));
    }

    private static class FeatureConfigComparator implements Comparator<FeatureConfig> {

        @Override
        public int compare(FeatureConfig o1, FeatureConfig o2) {
            // compare priority
            Optional<FeatureConfig.Description> descriptionO1 = Optional.ofNullable(o1.getDescription());
            var priorityO1 = descriptionO1
                    .map(FeatureConfig.Description::priority)
                    .orElse(0L);

            Optional<FeatureConfig.Description> descriptionO2 = Optional.ofNullable(o2.getDescription());
            var priorityO2 = descriptionO2
                    .map(FeatureConfig.Description::priority)
                    .orElse(0L);

            if (priorityO1.compareTo(priorityO2) != 0) {
                return priorityO1.compareTo(priorityO2);
            }

            // compare updatedAt reversed
            var updatedAtO1 = descriptionO1
                    .map(FeatureConfig.Description::updatedAt)
                    .orElse(LocalDateTime.MIN);

            var updatedAtO2 = descriptionO2
                    .map(FeatureConfig.Description::updatedAt)
                    .orElse(LocalDateTime.MIN);

            if (!updatedAtO2.isEqual(updatedAtO1)) {
                return updatedAtO2.compareTo(updatedAtO1);
            }

            // then compare createdAt reversed
            var createdAtO1 = descriptionO1
                    .map(FeatureConfig.Description::createdAt)
                    .orElse(LocalDateTime.MIN);
            var createdAtO2 = descriptionO2
                    .map(FeatureConfig.Description::createdAt)
                    .orElse(LocalDateTime.MIN);

            return createdAtO2.compareTo(createdAtO1);
        }
    }
}
