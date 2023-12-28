package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.persistent.experiment.db.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Implement Assignments.
 */
@Component
@RequiredArgsConstructor
public class AssignmentsImpl implements Experiment.Assignments {
    private final AssignmentRepository assignmentRepository;

    @Override
    public Mono<Void> updateBucketKey(Assignment.AssignmentId assignmentId, String bucketKey) {
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
    public Mono<Assignment> update(Assignment assignment) {
        return null;
    }
}
