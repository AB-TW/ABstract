package cn.dpc.ab.domain.experiment;

import cn.dpc.ab.domain.core.CustomerId;
import lombok.*;
import reactor.core.publisher.Mono;

import java.util.*;

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

    private Description description;

    /**
     * AssignmentId represents the ID of an assignment.
     */
    public record AssignmentId(Experiment.ExperimentId experimentId, String clientId) {

    }

    /**
     * Description represents the description of an assignment.
     */
    @Builder
    public record Description(Experiment.Bucket bucket,
                              long experimentConfigVersion,
                              Map<String, Object> extraInfo) {
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
                .description(Description.builder()
                        .experimentConfigVersion(experiment.getDescription().configVersion())
                        .build())
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
        Map<String, Object> extraInfo = Optional.ofNullable(description).orElse(
                Description.builder()
                        .extraInfo(new HashMap<>())
                        .build()
        ).extraInfo;
        Set<String> customerIds = (Set<String>) extraInfo.getOrDefault(CUSTOMER_ID_KEY, new HashSet<String>());

        if (Optional.ofNullable(customerId).filter(s -> !s.isEmpty()).isEmpty()) {
            return;
        }

        customerIds.add(customerId);
        extraInfo.put(CUSTOMER_ID_KEY, customerIds);
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

        Map<String, Object> extraInfo = Optional.ofNullable(description).orElse(
                Description.builder()
                        .extraInfo(new HashMap<>())
                        .build()
        ).extraInfo;

        Set<String> customerIds = (Set<String>) extraInfo.getOrDefault(CUSTOMER_ID_KEY, new HashSet<String>());
        return customerIds.contains(customerId.customerId());
    }

    /**
     * updateBucketKey modify bucketKey.
     *
     * @param bucketKey the bucket key
     * @return assignment
     */
    public Assignment updateBucketKey(Experiment experiment, String bucketKey) {
        //todo: 抛异常和校验experiment中是否存在对应bucketKey的写法感觉写的很怪
        if (!bucketKey.isEmpty()) {
            if (experiment.getDescription().buckets().stream()
                    .noneMatch(bucket -> bucket.key().equals(bucketKey))) {
                throw new IllegalArgumentException(
                        "Assignment not found: " + id
                );
            }
        }
        this.setDescription(Description.builder()
                        .bucket(Experiment.Bucket.builder()
                                .key(bucketKey)
                                .build())
                        .experimentConfigVersion(
                                experiment.getDescription().configVersion())
                        .extraInfo(this.description.extraInfo)
                .build());
        return this;
    }
}
