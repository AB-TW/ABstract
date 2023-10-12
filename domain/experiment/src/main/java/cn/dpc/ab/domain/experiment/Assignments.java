package cn.dpc.ab.domain.experiment;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Assignments domain.
 */
public interface Assignments {

    Flux<Assignment> getAssignmentByExperimentId(String experimentId);

    Mono<Assignment> add(Assignment assignment);

    Mono<Assignment> get(Assignment.AssignmentId assignmentId);
}
