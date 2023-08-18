package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Assignments;
import reactor.core.publisher.Mono;

/**
 * AssignmentImpl implements Assignments.
 */
public class AssignmentImpl implements Assignments {
    @Override
    public Mono<Assignment> getAssignmentById(String featureKey, String customerId, String clientId) {
        return null;
    }
}
