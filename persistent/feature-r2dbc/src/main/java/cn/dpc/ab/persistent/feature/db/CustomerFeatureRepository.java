package cn.dpc.ab.persistent.feature.db;

import cn.dpc.ab.domain.feature.Features;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * CustomerFeatureRepository is the repository for FeatureDb.
 */
@Repository
@RequiredArgsConstructor
public class CustomerFeatureRepository {
    private final R2dbcEntityTemplate template;

    public Flux<FeatureDb> findAll(Features.FilterParams params) {
        return template.select(Query.query(getCriteria(params)), FeatureDb.class);
    }

    public Flux<FeatureDb> findAll(Features.FilterParams params, Pageable pageable) {
        return template.select(Query.query(getCriteria(params)).offset(pageable.getOffset())
                .limit(pageable.getPageSize()).sort(pageable.getSort()), FeatureDb.class);
    }

    public Mono<Long> count(Features.FilterParams params) {
        return template.count(Query.query(getCriteria(params)), FeatureDb.class);
    }

    private static Criteria getCriteria(Features.FilterParams params) {
        Criteria criteria = Criteria.empty();
        if (StringUtils.hasLength(params.key())) {
            criteria = criteria.and("featureKey").like("%" + params.key() + "%");
        }

        if (params.enabled() != null) {
            criteria = criteria.and("enabled").is(params.enabled());
        }
        return criteria;
    }

}
