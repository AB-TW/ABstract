package cn.dpc.ab.persistent.experiment.db;

import com.couchbase.client.java.query.QueryScanConsistency;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * ExperimentDbRepository provides a skeleton implementation of the ExperimentDbRepository interface.
 */
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)

public interface ExperimentDbRepository extends ReactiveCrudRepository<ExperimentDb, String> {
}
