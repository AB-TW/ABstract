package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.exception.RecordNotFoundException;
import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.persistent.experiment.db.AssignmentDb;
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
    private final AssignmentRepository repository;
    private final Experiment experiment;

    @Override
    public Mono<Void> updateBucketKey(Assignment.AssignmentId assignmentId, String bucketKey) {
        return repository.findAssignmentDbByExperimentIdAndClientId(assignmentId.experimentId().id(), assignmentId.clientId())
                .switchIfEmpty(Mono.error(new RecordNotFoundException(assignmentId.experimentId().id())))
                .flatMap(db -> {
                    db.updateDescription(db.to(experiment).updateBucketKey(experiment, bucketKey).getDescription());
                    return repository.save(db);
                })
                .then();
    }

    @Override
    public Mono<Assignment> add(Assignment assignment) {
        return repository.save(AssignmentDb.from(assignment))
                .map(db -> db.to(experiment));
    }

    @Override
    public Mono<Assignment> get(Assignment.AssignmentId assignmentId) {
        return repository.findAssignmentDbByExperimentIdAndClientId(assignmentId.experimentId().id(), assignmentId.clientId())
                .map(db -> db.to(experiment));
    }

    @Override
    public Mono<Assignment> update(Assignment assignment) {
        return repository.findAssignmentDbByExperimentIdAndClientId(assignment.getId().experimentId().id(), assignment.getId().clientId())
                .switchIfEmpty(Mono.error(new RecordNotFoundException(assignment.getId().experimentId().id())))
                .flatMap(db -> {
                    db.updateDescription(db.to(experiment).updateDescription(assignment.getDescription()).getDescription());
                    return repository.save(db);
                })
                .map(db -> db.to(experiment));
    }

    @Override
    public Mono<? extends Assignment> addOrUpdate(Assignment assignment) {
        return null;
    }


}
