package cn.dpc.ab.domain.experiment;

import cn.dpc.ab.domain.core.CustomerId;
import lombok.*;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        if (experiment.getDescription().status().getStatus() != Experiment.Status.DynamicStatus.COLLECTING) {
            return Mono.just(EMPTY);
        }

        if (!Optional.ofNullable(experiment.getDescription())
                .map(Experiment.Description::filterCondition)
                .map(filterCondition -> filterCondition.match(customerId))
                .orElse(true)) {
            return Mono.just(getDefaultAssignment(experiment, customerId));
        }

        AtomicInteger percentage = new AtomicInteger(0);
        for (Experiment.Bucket bucket : Optional.ofNullable(experiment.getDescription())
                .map(Experiment.Description::buckets).orElse(new ArrayList<>())) {
            percentage.set(percentage.get() + bucket.percentage());
            if (Optional.ofNullable(customerId.clientId())
                    .filter(id -> !id.isEmpty())
                    .map(id -> Math.floorMod((experiment.getId().id() + id).hashCode(), 100) < percentage.get())
                    .orElse(false)) {
                return Mono.just(Assignment.builder()
                        .id(new AssignmentId(experiment.getId(), customerId.clientId()))
                        .description(Description.builder()
                                .bucket(bucket)
                                .experimentConfigVersion(experiment.getDescription().configVersion())
                                .build())
                        .build());
            }
        }

        return Mono.just(getDefaultAssignment(experiment, customerId));
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
        Map<String, Object> extraInfo = Optional.ofNullable(this.description)
                .flatMap(d -> Optional.ofNullable(d.extraInfo))
                .orElse(new HashMap<>());
        Set<String> customerIds = new HashSet<>((ArrayList<String>) extraInfo.getOrDefault(CUSTOMER_ID_KEY, new ArrayList<>()));

        if (Optional.ofNullable(customerId).filter(s -> !s.isEmpty()).isEmpty()) {
            return;
        }

        customerIds.add(customerId);
        extraInfo.put(CUSTOMER_ID_KEY, customerIds);
        this.description = Description.builder()
                .extraInfo(extraInfo)
                .bucket(this.description.bucket)
                .experimentConfigVersion(this.description.experimentConfigVersion)
                .build();
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

        Map<String, Object> extraInfo = Optional.ofNullable(description)
                .flatMap(d -> Optional.ofNullable(d.extraInfo))
                .orElse(new HashMap<>());

        Set<String> customerIds = new HashSet<>((ArrayList<String>) extraInfo.getOrDefault(CUSTOMER_ID_KEY, new ArrayList<>()));
        return customerIds.contains(customerId.customerId());
    }

    /**
     * updateBucketKey modify bucketKey.
     *
     * @param bucketKey the bucket key
     * @return assignment
     */
    public Assignment updateBucketKey(Experiment experiment, String bucketKey) {
        if (Optional.ofNullable(bucketKey).map(k -> !k.isEmpty()).orElse(false)
                && experiment.getDescription().buckets().stream()
                .noneMatch(bucket -> bucket.key().equals(bucketKey))) {
            throw new IllegalArgumentException(
                    "Bucket Key not found: " + bucketKey
            );
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

    /**
     * Update the description of the assignment.
     *
     * @param description the new description of the assignment
     * @return the assignment.
     */
    public Assignment updateDescription(Description description) {
        this.description = Description.builder()
                .bucket(description.bucket)
                .experimentConfigVersion(description.experimentConfigVersion)
                .extraInfo(description.extraInfo)
                .build();
        return this;
    }
}
