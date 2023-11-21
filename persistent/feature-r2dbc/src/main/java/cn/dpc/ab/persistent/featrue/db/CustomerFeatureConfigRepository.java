package cn.dpc.ab.persistent.featrue.db;

import cn.dpc.ab.domain.feature.Feature;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


/**
 * FeatureRepository is the repository of FeatureDb.
 */
@Repository
@RequiredArgsConstructor
public class CustomerFeatureConfigRepository {
    private final R2dbcEntityTemplate template;

    /**
     * findAll retrieves all feature configs from the database.
     *
     * @Param featureKey the feature key
     * @Param params     the filter params
     * @return the feature configs
     */
    public Flux<FeatureConfigDb> findAll(String featureKey, Feature.Configs.FilterParams params) {
        return template.select(Query.query(getCriteria(featureKey, params)), FeatureConfigDb.class);
    }

    /**
     * findAll retrieves all feature configs from the database.
     *
     * @Param featureKey the feature key
     * @Param params     the filter params
     * @Param pageable   the pageable
     * @return the feature configs
     */
    public Flux<FeatureConfigDb> findAll(String featureKey, Feature.Configs.FilterParams params, Pageable pageable) {
        return template.select(Query.query(getCriteria(featureKey, params))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .sort(pageable.getSort()), FeatureConfigDb.class);
    }

    /**
     * count counts the number of feature configs in the database.
     *
     * @Param featureKey the feature key
     * @Param params     the filter params
     * @return the number of feature configs
     */
    public Mono<Long> count(String featureKey, Feature.Configs.FilterParams params) {
        return template.count(Query.query(getCriteria(featureKey, params)), FeatureConfigDb.class);
    }

    private Criteria getCriteria(String featureKey, Feature.Configs.FilterParams params) {
        Criteria criteria = Criteria.empty().and("featureKey").is(featureKey);

        if (StringUtils.hasLength(params.title())) {
            criteria = criteria.and(Criteria.empty().or("title").like("%" + params.title() + "%")
                    .or("description").like("%" + params.title() + "%")
            );
        }

        if (params.status() != null) {
            switch (params.status()) {
                case DRAFT -> criteria = criteria.and("staticStatus").is("DRAFT");
                case DISABLED -> criteria = criteria.and("staticStatus").is("DISABLED");
                case NOT_BEGIN -> criteria = criteria.and("staticStatus").is("PUBLISHED")
                        .and("startTime").greaterThan(LocalDateTime.now());
                case RUNNING -> criteria = criteria.and("staticStatus").is("PUBLISHED")
                        .and(Criteria.where("startTime").isNull()
                                .or("startTime").lessThanOrEquals(LocalDateTime.now()))
                        .and(Criteria.where("endTime").isNull()
                                .or("endTime").greaterThan(LocalDateTime.now()));
                case EXPIRED -> criteria = criteria.and("staticStatus").is("PUBLISHED")
                        .and("endTime").lessThanOrEquals(LocalDateTime.now());
                default -> {
                }
            }
        } else {
            criteria = criteria.and("staticStatus").not("DELETED");
        }
        return criteria;
    }
}
