package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Assignments;
import cn.dpc.ab.domain.experiment.Experiment;
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
    public Mono<Void> update(String experimentId, String clientId, String bucketKey) {
        Assignment.AssignmentId assignmentId = new Assignment.AssignmentId(
                new Experiment.ExperimentId(experimentId), clientId);
        return this.getAssignmentById(assignmentId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "Assignment not found: " + AssignmentDb.idFrom(assignmentId)
                )))
                .flatMap(assignment -> updateToDb(assignment, bucketKey));
    }

    @Override
    public Mono<Assignment> add(Assignment assignment) {
        return repository.save(AssignmentDb.from(assignment))
                .map(AssignmentDb::toAssignment);
    }

    @Override
    public Mono<Assignment> get(Assignment.AssignmentId assignmentId) {
        return null;
    }

    @Override
    public Mono<Assignment> getAssignmentById(Assignment.AssignmentId assignmentId) {
        return repository.findById(AssignmentDb.idFrom(assignmentId))
                .map(AssignmentDb::toAssignment);
    }

    /**
     * updateToDb updates a assignment in the database.
     *
     * @param assignment  the assignment
     * @param bucketKey the bucket key
     * @return a Mono of Void
     */
    private Mono<Void> updateToDb(Assignment assignment, String bucketKey) {
        return repository.findById(AssignmentDb.idFrom(assignment.getId()))
                .flatMap(db -> {
                    db.updateBucketKey(bucketKey);
                    return repository.save(db);
                }).then();
    }
}
