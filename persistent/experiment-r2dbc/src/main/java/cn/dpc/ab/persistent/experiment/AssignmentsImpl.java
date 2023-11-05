package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Assignments;
import cn.dpc.ab.persistent.experiment.db.AssignmentRepository;
import cn.dpc.ab.persistent.experiment.db.CustomerAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implement Assignments.
 */
@Component
@RequiredArgsConstructor
public class AssignmentsImpl implements Assignments {
    private final CustomerAssignmentRepository customerAssignmentRepository;
    private final AssignmentRepository assignmentRepository;

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
