package cn.dpc.ab.persistent.experiment.db;

import com.couchbase.client.java.query.QueryScanConsistency;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.data.couchbase.repository.ScanConsistency;

/**
 * AssignmentDbRepository provides a skeleton implementation of the AssignmentDbRepository interface.
 */
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public interface AssignmentDbRepository extends ReactiveCouchbaseRepository<AssignmentDb, String> {
}
