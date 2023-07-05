package cn.dpc.ab.persistent.customer;

import cn.dpc.ab.domain.customer.Customer;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.persistent.feature.db.FeatureConfigDb;
import cn.dpc.ab.persistent.feature.db.FeatureConfigDbRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

/**
 * CustomerFeatureConfigs implements Customer.FeatureConfigs.
 */
@RequiredArgsConstructor
public class CustomerFeatureConfigs implements Customer.FeatureConfigs {
    private final FeatureConfigDbRepository featureConfigRepository;
    private final Customer.CustomerId customerId;


    @Override
    public Flux<FeatureConfig> getByFeatureKey(Feature.FeatureId featureKey) {
        return featureConfigRepository.findByFeatureKey(featureKey.featureKey())
                .map(FeatureConfigDb::toFeatureConfig);
        // TODO: filter by customer info
    }
}
