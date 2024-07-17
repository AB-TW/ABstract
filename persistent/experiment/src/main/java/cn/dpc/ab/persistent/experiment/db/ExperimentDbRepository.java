package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import com.couchbase.client.java.query.QueryScanConsistency;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.stream.Collectors;

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
            AND ( \
                #{#param.featureKey == null || #param.featureKey == ''} \
                OR featureKey = '#{#param.featureKey}' \
            ) \
            #{#param.statusQuery}
            """;

    /**
     * FilterParams.
     */
    record FilterParams(String key, String featureKey, String statusQuery) {

        /**
         * From.
         *
         * @param filterParams the filter params
         * @return the filter params
         */
        public static FilterParams from(Experiments.FilterParams filterParams) {
            StringBuilder statusQuery = new StringBuilder();
            if (CollectionUtils.isEmpty(filterParams.status())) {
                return new FilterParams(filterParams.key(), filterParams.featureKey(), statusQuery.toString());
            }
            statusQuery.append("AND (");

            statusQuery.append(filterParams.status()
                    .stream()
                    .map(FilterParams::getStatusQueryString)
                    .collect(Collectors.joining(" OR ")));
            statusQuery.append(")");
            return new FilterParams(filterParams.key(), filterParams.featureKey(), statusQuery.toString());
        }

        private static String getStatusQueryString(Experiment.Status.DynamicStatus status) {
            return switch (status) {
                case DRAFT -> "`status`.`staticStatus` = 'DRAFT'";
                case NOT_BEGIN -> """
                        `status`.`staticStatus` = 'PUBLISHED' \
                        AND `status`.`startTime` > CLOCK_MILLIS()
                        """;
                case COLLECTING -> """
                        `status`.`staticStatus` = 'PUBLISHED' \
                        AND (`status`.`startTime` IS NOT VALUED OR `status`.`startTime` <= CLOCK_MILLIS()) \
                        AND (`status`.`endCollectTime` IS NOT VALUED OR `status`.`endCollectTime` > CLOCK_MILLIS())
                        """;
                case FINISH_COLLECT -> """
                        `status`.`staticStatus` = 'PUBLISHED' \
                        AND `status`.`endCollectTime` <= CLOCK_MILLIS()
                        """;
                case PAUSED -> "`status`.`staticStatus` = 'PAUSED'";
                case STOPPED -> """
                        `status`.`staticStatus` = 'STOPPED' \
                        OR (`status`.`staticStatus` = 'PUBLISHED' AND `status`.`endTime` <= CLOCK_MILLIS()) \
                        """;
                default -> "";
            };
        }
    }

    @Query("#{#n1ql.selectEntity} WHERE " + FILTER_CONDITION)
    Flux<ExperimentDb> findByFilterParams(@Param("param") FilterParams filterParams, Pageable pageable);

    @Query("select count(1) FROM #{#n1ql.bucket} WHERE " + FILTER_CONDITION)
    Mono<Long> countByFilterParams(@Param("param") FilterParams params);
}
