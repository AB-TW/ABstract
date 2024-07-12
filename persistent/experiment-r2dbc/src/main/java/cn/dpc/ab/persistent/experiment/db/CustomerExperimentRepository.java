package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * CustomerExperimentRepository is the repository of ExperimentDb.
 */
@Repository
@RequiredArgsConstructor
public class CustomerExperimentRepository {
    private final R2dbcEntityTemplate template;

    /**
     * findAll retrieves all experiments from the database.
     *
     * @param params the filter params
     * @return the experiments
     */
    public Flux<ExperimentDb> findAll(Experiments.FilterParams params) {
        return template.select(Query.query(getCriteria(params)), ExperimentDb.class);
    }


    /**
     * findAll retrieves all experiments from the database.
     *
     * @param params   the filter params
     * @param pageable the pageable
     * @return the experiments
     */
    public Flux<ExperimentDb> findAll(Experiments.FilterParams params, Pageable pageable) {
        return template.select(Query.query(getCriteria(params))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .sort(pageable.getSort()), ExperimentDb.class);
    }

    /**
     * count counts the number of experiments in the database.
     *
     * @param params the filter params
     * @return the number of experiments
     */
    public Mono<Long> count(Experiments.FilterParams params) {
        return template.count(Query.query(getCriteria(params)), ExperimentDb.class);
    }


    /**
     * count counts the number of experiments in the database.
     *
     * @param params the filter params
     * @return the number of experiments
     */
    private CriteriaDefinition getCriteria(Experiments.FilterParams params) {
        Criteria criteria = Criteria.empty();

        if (StringUtils.hasLength(params.key())) {
            criteria = criteria.and(Criteria.where("featureKey").like("%" + params.key() + "%")
                    .or("name").like("%" + params.key() + "%"));
        }

        if (StringUtils.hasLength(params.featureKey())) {
            criteria = criteria.and("featureKey").is(params.featureKey());
        }

        if (CollectionUtils.isEmpty(params.status())) {
            return criteria;
        }

        Criteria criteriaStatus = params.status()
                .stream()
                .map(this::getStatusCriteria)
                .reduce(Criteria::or)
                .orElse(Criteria.empty());

        criteria = criteria.and(criteriaStatus);

        return criteria;
    }

    private Criteria getStatusCriteria(Experiment.Status.DynamicStatus status) {
        return switch (status) {
            case DRAFT -> Criteria.where("staticStatus").is("DRAFT");
            case NOT_BEGIN -> Criteria.where("staticStatus").is("PUBLISHED")
                    .and("startTime").greaterThan(LocalDateTime.now());
            case COLLECTING -> Criteria.where("staticStatus").is("PUBLISHED")
                    .and(Criteria.where("startTime").isNull()
                            .or("startTime").lessThanOrEquals(LocalDateTime.now()))
                    .and(Criteria.where("endCollectTime").isNull()
                            .or("endCollectTime").greaterThan(LocalDateTime.now()));
            case FINISH_COLLECT -> Criteria.where("staticStatus").is("PUBLISHED")
                    .and("endCollectTime").lessThanOrEquals(LocalDateTime.now());
            case PAUSED -> Criteria.where("staticStatus").is("PAUSED");
            case STOPPED -> Criteria.where("staticStatus").is("STOPPED")
                    .or(Criteria.where("staticStatus").is("PUBLISHED")
                            .and("endTime").lessThanOrEquals(LocalDateTime.now()));
            default -> Criteria.empty();
        };
    }

}
