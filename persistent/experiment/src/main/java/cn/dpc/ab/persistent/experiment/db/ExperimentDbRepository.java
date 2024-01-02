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
            AND ( \
                #{#param.key == null || #param.key == ''} \
                OR featureKey like '%#{#param.key}%' \
                OR name like '%#{#param.key}%' \
            ) \
            #{#param.statusQuery}
            """;

    /**
     * FilterParams.
     */
    record FilterParams(String key, String statusQuery) {

        /**
         * From.
         *
         * @param filterParams the filter params
         * @return the filter params
         */
        public static FilterParams from(Experiments.FilterParams filterParams) {
            String statusQuery = "";
            if (null == filterParams.status()) {
                return new FilterParams(filterParams.key(), statusQuery);
            }

            switch (filterParams.status()) {
                case DRAFT -> statusQuery += "AND `status`.`staticStatus` = 'DRAFT'";
                case NOT_BEGIN -> statusQuery += """
                        AND `status`.`staticStatus` = 'PUBLISHED' \
                        AND `status`.`startTime` > CLOCK_MILLIS()
                        """;
                case COLLECTING -> statusQuery += """
                        AND `status`.`staticStatus` = 'PUBLISHED' \
                        AND (`status`.`startTime` IS NOT VALUED OR `status`.`startTime` <= CLOCK_MILLIS()) \
                        AND (`status`.`endCollectTime` IS NOT VALUED OR `status`.`endCollectTime` > CLOCK_MILLIS())
                        """;
                case FINISH_COLLECT -> statusQuery += """
                        AND `status`.`staticStatus` = 'PUBLISHED' \
                        AND `status`.`endCollectTime` <= CLOCK_MILLIS()
                        """;
                case PAUSED -> statusQuery += "AND `status`.`staticStatus` = 'PAUSED'";
                case STOPPED -> statusQuery += """
                        AND (`status`.`staticStatus` = 'STOPPED' \
                        OR (`status`.`staticStatus` = 'PUBLISHED' AND `status`.`endTime` <= CLOCK_MILLIS()) \
                        """;
                default -> {
                }
            }
            return new FilterParams(filterParams.key(), statusQuery);
        }
    }


    //TODO 根据实验截止时间倒序排列
    Flux<ExperimentDb> findByFeatureKey(String featureKey, Sort sort);

    @Query("#{#n1ql.selectEntity} WHERE " + FILTER_CONDITION)
    Flux<ExperimentDb> findByFilterParams(@Param("param") FilterParams filterParams, Pageable pageable);

    @Query("select count(1) FROM #{#n1ql.bucket} WHERE " + FILTER_CONDITION)
    Mono<Long> countByFilterParams(@Param("param") FilterParams params);
}
