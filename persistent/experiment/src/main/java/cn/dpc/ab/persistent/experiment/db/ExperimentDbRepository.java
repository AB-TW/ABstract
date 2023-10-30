package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Experiments;
import com.couchbase.client.java.query.QueryScanConsistency;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * ExperimentDbRepository provides a skeleton implementation of the ExperimentDbRepository interface.
 */
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public interface ExperimentDbRepository extends ReactiveCrudRepository<ExperimentDb, String> {

    String FILTER_CONDITION = """
            #{#n1ql.filter} \
            AND (#{#param.key == null || #param.key == ''} OR featureKey like '%#{#param.key}%') \
            AND (#{#param.status != null} OR status.staticStatus like '%#{#param.status}%')
            """;

    //TODO 根据实验截止时间倒序排列
    Flux<ExperimentDb> findByFeatureKey(String featureKey, Sort sort);

    @Query("#{#n1ql.selectEntity} WHERE " + FILTER_CONDITION)
    Flux<ExperimentDb> findByFilterParams(@Param("param") Experiments.FilterParams filterParams,
                                       Pageable pageable);

    @Query("select count(1) FROM #{#n1ql.bucket} WHERE " + FILTER_CONDITION)
    Mono<Long> countByFilterParams(@Param("param") Experiments.FilterParams params);
}
