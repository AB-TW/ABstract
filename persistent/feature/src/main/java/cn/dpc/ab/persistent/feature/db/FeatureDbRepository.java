package cn.dpc.ab.persistent.feature.db;

import cn.dpc.ab.domain.feature.Features;
import com.couchbase.client.java.query.QueryScanConsistency;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * FeatureDBRepository provides a skeleton implementation of the FeatureDBRepository interface.
 */
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public interface FeatureDbRepository extends ReactiveCouchbaseRepository<FeatureDb, String> {
    String FILTER_CONDITION = "#{#n1ql.filter} AND (#{#param.key == null || #param.key == ''} OR featureKey like '%#{#param.key}%') "
            + "AND (#{#param.enabled == null} OR description.enabled = #{#param.enabled == null ? true: #param.enabled}) ";

    @Query("#{#n1ql.selectEntity} WHERE " + FILTER_CONDITION)
    Flux<FeatureDb> findByFilterParams(@Param("param") Features.FilterParams filterParams);

    @Query("#{#n1ql.selectEntity} WHERE " + FILTER_CONDITION)
    Flux<FeatureDb> findByFilterParams(@Param("param") Features.FilterParams filterParams,
                                       Pageable pageable);

    @Query("select count(1) FROM #{#n1ql.bucket} WHERE " + FILTER_CONDITION)
    Mono<Long> countByFilterParams(@Param("param") Features.FilterParams filterParams);
}
