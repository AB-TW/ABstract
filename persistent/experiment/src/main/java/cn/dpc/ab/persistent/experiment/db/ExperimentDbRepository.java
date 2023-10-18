package cn.dpc.ab.persistent.experiment.db;

import com.couchbase.client.java.query.QueryScanConsistency;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * ExperimentDbRepository provides a skeleton implementation of the ExperimentDbRepository interface.
 */
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public interface ExperimentDbRepository extends ReactiveCrudRepository<ExperimentDb, String> {
    //TODO 根据实验截止时间倒序排列

    Flux<ExperimentDb> findByFeatureKey(String featureKey, Sort sort);
}
