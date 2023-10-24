package cn.dpc.ab.domain.experiment;

import cn.dpc.ab.domain.core.CustomerId;
import lombok.*;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Assignment represents an assignment.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Assignment {
    private static final String CUSTOMER_ID_KEY = "customerIds";

    public static final Assignment EMPTY = new Assignment();

    private AssignmentId id;

    private Experiment.Bucket bucket;

    private long experimentConfigVersion;

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
                .build();

        if (Optional.ofNullable(customerId.customerId()).filter(s -> !s.isEmpty()).isPresent()) {
            assignment.addCustomerId(customerId.customerId());
        }

        return assignment;
    }

    /**
     * add customerId to extraInfo.
     *
     * @param customerId the customerId to add
     */
    public void addCustomerId(String customerId) {
        Set<String> customerIds = (Set<String>) extraInfo.getOrDefault(CUSTOMER_ID_KEY, new HashSet<String>());

        if (Optional.ofNullable(customerId).filter(s -> !s.isEmpty()).isEmpty()) {
            return;
        }

        customerIds.add(customerId);
        this.extraInfo.put(CUSTOMER_ID_KEY, customerIds);
    }

    /**
     * contains customerId.
     *
     * @param customerId the customerId to check
     * @return true if contains
     */
    public boolean containsCustomerId(CustomerId customerId) {
        if (Optional.ofNullable(customerId.customerId()).filter(s -> !s.isEmpty()).isEmpty()) {
            return false;
        }

        Set<String> customerIds = (Set<String>) extraInfo.getOrDefault(CUSTOMER_ID_KEY, new HashSet<String>());
        return customerIds.contains(customerId.customerId());
    }
}
