package cn.dpc.ab.persistent.experiment.db;

import com.couchbase.client.java.ReactiveCluster;
import com.couchbase.client.java.query.QueryOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import static com.couchbase.client.java.query.QueryScanConsistency.REQUEST_PLUS;

/**
 * AssignmentStatisticsRepository.
 */
@Repository
@RequiredArgsConstructor
public class AssignmentStatisticsRepository {
    private final ReactiveCouchbaseTemplate template;
    private final ReactiveCluster couchbaseReactiveCluster;

    /**
     * Statistic Count By BucketKey.
     *
     * @param experimentId Experiment Id
     */
    public Flux<BucketCount> statisticCountByBucketKey(String experimentId) {
        String n1ql = "SELECT bucketKey, COUNT(1) as count FROM `"
                + template.getBucketName()
                + "` WHERE _class='assignment' AND experimentId='"
                + experimentId
                + "' GROUP BY bucketKey";
        return couchbaseReactiveCluster.query(n1ql, QueryOptions.queryOptions().scanConsistency(REQUEST_PLUS))
                .flatMapMany(result -> result.rowsAs(BucketCount.class));
    }
}
