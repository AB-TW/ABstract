package cn.dpc.ab.persistent.experiment.db;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * CustomerAssignmentRepository is the repository of AssignmentDb.
 */
@Repository
@RequiredArgsConstructor
public class CustomerAssignmentRepository {
    private final R2dbcEntityTemplate template;

    /**
     * findAll retrieves all assignments from the database.
     *
     * @param experimentId the experiment id
     * @return the assignments
     */
    public Flux<AssignmentDb> findAll(String experimentId) {
        return template.select(Query.query(getCriteria(experimentId)), AssignmentDb.class);
    }


    /**
     * findAll retrieves all assignments from the database.
     *
     * @param experimentId the experiment id
     * @param pageable     the pageable
     * @return the assignments
     */
    public Flux<AssignmentDb> findAll(String experimentId, Pageable pageable) {
        return template.select(Query.query(getCriteria(experimentId)).offset(pageable.getOffset())
                .limit(pageable.getPageSize()).sort(pageable.getSort()), AssignmentDb.class);
    }


    /**
     * count counts the number of assignments in the database.
     *
     * @param experimentId the experiment id
     * @return the number of assignments
     */
    public Mono<Long> count(String experimentId) {
        return template.count(Query.query(getCriteria(experimentId)), AssignmentDb.class);
    }

    private CriteriaDefinition getCriteria(String experimentId) {
        Criteria criteria = Criteria.empty();
        if (experimentId != null) {
            criteria = criteria.and("experimentId").is(experimentId);
        }
        return criteria;
    }
}
