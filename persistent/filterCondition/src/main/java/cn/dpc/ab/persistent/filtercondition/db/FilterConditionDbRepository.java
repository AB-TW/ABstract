package cn.dpc.ab.persistent.filtercondition.db;

import cn.dpc.ab.domain.core.FilterConditions;
import com.couchbase.client.java.query.QueryScanConsistency;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * FilterConditionDbRepository provides a skeleton implementation of the FilterConditionDbRepository interface.
 */
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public interface FilterConditionDbRepository extends ReactiveCrudRepository<FilterConditionTemplateDb, String> {

    String FILTER_CONDITION = """
            #{#n1ql.filter} \
            AND ( \
                #{#param.key == null || #param.key == ''} \
                OR id like '%#{#param.key}%' \
                OR templateName like '%#{#param.key}%' \
            ) \
            """;

    @Query("#{#n1ql.selectEntity} WHERE " + FILTER_CONDITION)
    Flux<FilterConditionTemplateDb> findByFilterParams(@Param("param") FilterConditions.FilterParams filterParams, Pageable pageable);

    @Query("select count(1) FROM #{#n1ql.bucket} WHERE " + FILTER_CONDITION)
    Mono<Long> countByFilterParams(@Param("param") FilterConditions.FilterParams params);

}
