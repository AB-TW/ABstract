package cn.dpc.ab.domain.experiment;

import reactor.core.publisher.Mono;

/**
 * Assignments represents a collection of results of experiments.
 */

public interface Assignments {
    /**
     * getAssignmentById provides an Assignment of experiment by ids.
     */
    Mono<Assignment> getAssignmentById(String featureKey, String customerId, String clientId);
}
