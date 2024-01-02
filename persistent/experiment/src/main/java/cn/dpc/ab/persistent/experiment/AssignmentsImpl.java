package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.persistent.experiment.db.AssignmentDb;
import cn.dpc.ab.persistent.experiment.db.AssignmentDbRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * AssignmentImpl implements Assignments.
 */
@RequiredArgsConstructor
public class AssignmentsImpl implements Experiment.Assignments {

    private final AssignmentDbRepository repository;

    private final Experiment experiment;


    @Override
    public Mono<Void> updateBucketKey(Assignment.AssignmentId assignmentId, String bucketKey) {
        String id = AssignmentDb.idFrom(assignmentId);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Assignment not found: " + id)))
                .flatMap(db ->
                        repository.save(db.update(db.to(experiment).updateBucketKey(experiment, bucketKey).getDescription())))
                .then();
    }

    @Override
    public Mono<Assignment> add(Assignment assignment) {
        return repository.save(AssignmentDb.from(assignment))
                .map(db -> db.to(experiment));
    }

    @Override
    public Mono<Assignment> get(Assignment.AssignmentId assignmentId) {
        return repository.findById(AssignmentDb.idFrom(assignmentId))
                .map(db -> db.to(experiment));
    }

    /**
     * updates a bucket for assignment.
     *
     * @param assignment the assignment need to be updated
     * @return a Mono of Void
     */
    @Override
    public Mono<Assignment> update(Assignment assignment) {
        return repository.findById(AssignmentDb.idFrom(assignment.getId()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Assignment not found: " + AssignmentDb.idFrom(assignment.getId()))))
                .flatMap(assignmentDb -> repository.save(assignmentDb.update(assignmentDb.to(experiment)
                        .updateDescription(assignment.getDescription()).getDescription())))
                .map(assignmentDb -> assignmentDb.to(experiment));
    }

    /**
     * add or update assignment.
     *
     * @param assignment the assignment need to be added or updated
     * @return a Mono of Assignment
     */
    @Override
    public Mono<? extends Assignment> addOrUpdate(Assignment assignment) {
        return repository.findById(AssignmentDb.idFrom(assignment.getId()))
                .flatMap(assignmentDb -> repository.save(assignmentDb.update(assignmentDb.to(experiment)
                        .updateDescription(assignment.getDescription()).getDescription())))
                .map(assignmentDb -> assignmentDb.to(experiment))
                .switchIfEmpty(this.add(assignment));
    }


}
