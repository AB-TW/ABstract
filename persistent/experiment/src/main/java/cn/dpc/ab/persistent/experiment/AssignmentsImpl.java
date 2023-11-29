package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Assignments;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.persistent.experiment.db.AssignmentDb;
import cn.dpc.ab.persistent.experiment.db.AssignmentDbRepository;
import cn.dpc.ab.persistent.experiment.db.ExperimentDbRepository;
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

    private final AssignmentDbRepository repository;

    private final ExperimentDbRepository experimentDbRepository;

    @Override
    public Flux<Assignment> getAssignmentByExperimentId(String experimentId) {
        return null;
    }

    @Override
    public Mono<Void> updateBucketKey(Assignment.AssignmentId assignmentId, String bucketKey) {
        String experimentId = assignmentId.experimentId().id();
        return experimentDbRepository.findById(experimentId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "Experiment not found: " + experimentId
                )))
                .flatMap(experimentDb ->
                        saveBucketKey(experimentDb.toExperiment(), assignmentId, bucketKey));
    }

    @Override
    public Mono<Assignment> add(Assignment assignment) {
        return repository.save(AssignmentDb.from(assignment))
                .map(AssignmentDb::toAssignment);
    }

    @Override
    public Mono<Assignment> get(Assignment.AssignmentId assignmentId) {
        return repository.findById(AssignmentDb.idFrom(assignmentId))
                .map(AssignmentDb::toAssignment);
    }

    /**
     * updateBucketKey updates a bucket for assignment.
     *
     * @param assignmentId  the assignment id
     * @param bucketKey the bucket key
     * @return a Mono of Void
     */
    private Mono<Void> saveBucketKey(Experiment experiment,
                                     Assignment.AssignmentId assignmentId,
                                     String bucketKey) {
        String id = AssignmentDb.idFrom(assignmentId);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        "Assignment not found: " + id
                )))
                .flatMap(db -> repository.save(db.update(
                                db.to(experiment)
                                        .updateBucketKey(experiment, bucketKey).getDescription()
                ))).then();
    }
}
