package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Assignments;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implement Assignments.
 */
@Component
public class AssignmentsImpl implements Assignments {

    @Override
    public Flux<Assignment> getAssignmentByExperimentId(String experimentId) {
        return null;
    }

    @Override
    public Mono<Assignment> add(Assignment assignment) {
        return null;
    }

    @Override
    public Mono<Assignment> get(Assignment.AssignmentId assignmentId) {
        return null;
    }
}
