package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Assignments;
import cn.dpc.ab.persistent.experiment.db.AssignmentDb;
import cn.dpc.ab.persistent.experiment.db.AssignmentDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * AssignmentImpl implements Assignments.
 */
@Component
@RequiredArgsConstructor
public class AssignmentsImpl implements Assignments {

    private final AssignmentDbRepository repository;

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

    @Override
    public Mono<Assignment> getAssignmentById(Assignment.AssignmentId assignmentId) {
        return repository.findById("ci:" + assignmentId.clientId() + "-" + "ei:" + assignmentId.experimentId())
                .map(AssignmentDb::toAssignment);
    }
}
