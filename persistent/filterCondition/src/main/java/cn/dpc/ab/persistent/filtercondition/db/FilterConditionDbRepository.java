package cn.dpc.ab.persistent.filtercondition.db;

import com.couchbase.client.java.query.QueryScanConsistency;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * FilterConditionDbRepository provides a skeleton implementation of the FilterConditionDbRepository interface.
 */
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public interface FilterConditionDbRepository extends ReactiveCrudRepository<FilterConditionTemplateDb, String> {

}
