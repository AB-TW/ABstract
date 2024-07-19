package cn.dpc.ab.persistent.feature.db;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import com.couchbase.client.java.query.QueryScanConsistency;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * FeatureConfigDBRepository provides a skeleton implementation of the FeatureConfigDBRepository interface.
 */
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public interface FeatureConfigDbRepository extends ReactiveCrudRepository<FeatureConfigDb, String> {

    String FILTER_CONDITION = """
            #{#n1ql.filter} \
            AND `featureKey` = '#{#featureKey}' \
            AND (#{#param.title == null || #param.title == ''} \
            OR `description`.`title` like '%#{#param.title}%' OR `description`.`description` like '%#{#param.title}%') \
            #{#param.statusQuery}
            """;

    /**
     * FilterParams.
     */
    record FilterParams(String title, String statusQuery) {

        /**
         * From.
         *
         * @param filterParams the filter params
         * @return the filter params
         */
        public static FilterParams from(Feature.Configs.FilterParams filterParams) {
            String statusQuery = "AND `description`.`status`.`staticStatus` != 'DELETED' ";
            if (null == filterParams.status()) {
                return new FilterParams(filterParams.title(), statusQuery);
            }

            statusQuery += "AND (";
            statusQuery += filterParams.status()
                    .stream()
                    .map(FilterParams::getStatusQueryString)
                    .collect(Collectors.joining(" OR "));
            statusQuery += ")";

            return new FilterParams(filterParams.title(), statusQuery);
        }

        private static String getStatusQueryString(FeatureConfig.Status.DynamicStatus status) {
            return switch (status) {
                case DRAFT -> "`description`.`status`.`staticStatus` = 'DRAFT'";
                case DISABLED -> "`description`.`status`.`staticStatus` = 'DISABLED'";
                case NOT_BEGIN -> """
                        `description`.`status`.`staticStatus` = 'PUBLISHED' \
                        AND `description`.`status`.`startTime` > CLOCK_MILLIS()
                        """;
                case RUNNING -> """
                        `description`.`status`.`staticStatus` = 'PUBLISHED' \
                        AND (`description`.`status`.`startTime` IS NOT VALUED OR `description`.`status`.`startTime` <= CLOCK_MILLIS()) \
                        AND (`description`.`status`.`endTime` IS NOT VALUED OR `description`.`status`.`endTime` > CLOCK_MILLIS())
                        """;
                case EXPIRED -> """
                        `description`.`status`.`staticStatus` = 'PUBLISHED' \
                        AND `description`.`status`.`endTime` <= CLOCK_MILLIS()
                        """;
                default -> "";
            };
        }
    }

    /**
     * findByFeatureKey retrieves a feature from the database.
     *
     * @param featureKey the feature key
     * @return the feature
     */
    Flux<FeatureConfigDb> findByFeatureKey(String featureKey);


    @Query("#{#n1ql.selectEntity} WHERE " + FILTER_CONDITION)
    Flux<FeatureConfigDb> findByFilterParams(@Param("featureKey") String featureKey, @Param("param") FilterParams params);

    @Query("#{#n1ql.selectEntity} WHERE " + FILTER_CONDITION)
    Flux<FeatureConfigDb> findByFilterParams(@Param("featureKey") String featureKey, @Param("param") FilterParams params,
                                             Pageable pageable);

    @Query("select count(1) FROM #{#n1ql.bucket} WHERE " + FILTER_CONDITION)
    Mono<Long> countByFilterParams(@Param("featureKey") String featureKey, @Param("param") FilterParams params);

}
