package cn.dpc.ab.domain.experiment;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Assignments domain.
 */
public interface Assignments {

    Flux<Assignment> getAssignmentByExperimentId(String experimentId);

    Mono<Void> updateBucketKey(Assignment.AssignmentId assignmentId, String bucketKey);

    Mono<Assignment> add(Assignment assignment);

    /**
     * get provides an Assignment by id.
     */
    Mono<Assignment> get(Assignment.AssignmentId assignmentId);
}
