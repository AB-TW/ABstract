package cn.dpc.ab.persistent.experiment.db;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * ExperimentRepository provides a skeleton implementation of the ExperimentRepository interface.
 */
public interface AssignmentRepository extends R2dbcRepository<AssignmentDb, String> {
    Mono<AssignmentDb> findAssignmentDbByExperimentIdAndClientId(String experimentId, String clientId);

    @Query("SELECT bucket_key as bucketKey, COUNT(1) as `count`  FROM assignment WHERE experiment_id = :experimentId")
    Flux<BucketCount> statisticCountByBucketKey(String experimentId);
}
