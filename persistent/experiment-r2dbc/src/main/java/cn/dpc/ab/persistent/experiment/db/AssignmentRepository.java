package cn.dpc.ab.persistent.experiment.db;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * ExperimentRepository provides a skeleton implementation of the ExperimentRepository interface.
 */
public interface AssignmentRepository extends R2dbcRepository<AssignmentDb, String> {
    Mono<AssignmentDb> findAssignmentDbByExperimentIdAndClientId(String experimentId, String clientId);
}
