package cn.dpc.ab.persistent.feature.db;

import com.couchbase.client.java.query.QueryScanConsistency;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * FeatureDBRepository provides a skeleton implementation of the FeatureDBRepository interface.
 */
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public interface FeatureDbRepository extends ReactiveCrudRepository<FeatureDb, String> {

    Flux<FeatureDb> findAll();
}
