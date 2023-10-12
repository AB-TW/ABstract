package cn.dpc.ab.domain.experiment;

import cn.dpc.ab.domain.core.CustomerId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Assignment represents an assignment.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Assignment {

    public static final Assignment EMPTY = new Assignment();

    private AssignmentId id;
    private Experiment.Bucket bucket;

    private long experimentConfigVersion;
    private Set<String> customerIds;

    private Map<String, Object> extraInfo;

    /**
     * AssignmentId represents the ID of an assignment.
     */
    public record AssignmentId(Experiment.ExperimentId experimentId, String clientId) {

    }

    /**
     * createAssignment.
     */
    public static Mono<Assignment> createAssignment(Experiment experiment, CustomerId customerId) {

        //TODO add filter and assign algorithm

        if (experiment.getDescription().status().getStatus() != Experiment.Status.DynamicStatus.COLLECTING) {
            return Mono.just(EMPTY);
        }
        return Mono.just(getDefaultAssignment(experiment, customerId));
        //      return filterManager.filter(experiment.getDescription().filterCondition())
        //      .map(filterPass -> {
        //      if (filterPass) {
        //              return assignmentAlgorithm.assign(experiment, customerId.clientId(), customerId.customerId());
        //      }
        //
        //                    return getDefaultAssignment(experiment, customerId);
        //                });
    }

    private static Assignment getDefaultAssignment(Experiment experiment, CustomerId customerId) {
        Assignment assignment = Assignment.builder()
                .id(new AssignmentId(experiment.getId(), customerId.clientId()))
                .experimentConfigVersion(experiment.getDescription().configVersion())
                .customerIds(new HashSet<>()).build();

        if (Optional.ofNullable(customerId.customerId()).filter(s -> !s.isEmpty()).isPresent()) {
            assignment.addCustomerId(customerId.customerId());
        }

        return assignment;
    }

    /**
     * addCustomerId.
     */
    public void addCustomerId(String customerId) {
        this.customerIds = null == customerIds ? new HashSet<>() : customerIds;
        if (Optional.ofNullable(customerId).filter(s -> !s.isEmpty()).isPresent()) {
            customerIds.add(customerId);
        }
    }

    /**
     * containsCustomerId.
     */
    public boolean containsCustomerId(CustomerId customerId) {
        return Optional.ofNullable(customerId.customerId()).filter(s -> !s.isEmpty()).isPresent()
                && Optional.ofNullable(customerIds).map(ids -> ids.contains(customerId.customerId())).orElse(false);
    }
}
