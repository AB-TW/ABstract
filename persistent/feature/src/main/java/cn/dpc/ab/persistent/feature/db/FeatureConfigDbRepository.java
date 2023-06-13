package cn.dpc.ab.persistent.feature.db;

import com.couchbase.client.java.query.QueryScanConsistency;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * FeatureConfigDBRepository provides a skeleton implementation of the FeatureConfigDBRepository interface.
 */
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public interface FeatureConfigDbRepository extends ReactiveCrudRepository<FeatureConfigDb, String> {

    /**
     * findByFeatureKey retrieves a feature from the database.
     *
     * @param featureKey the feature key
     * @return the feature
     */
    Flux<FeatureConfigDb> findByFeatureKey(String featureKey);
}
