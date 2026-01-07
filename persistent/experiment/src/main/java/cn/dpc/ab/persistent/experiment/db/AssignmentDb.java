package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.IdAttribute;
import org.springframework.data.couchbase.core.mapping.id.IdPrefix;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.USE_ATTRIBUTES;

/**
 * AssignmentDb provides for save assignments.
 */
@TypeAlias("assignment")
@Document
@Data
@Builder
public class AssignmentDb {
    private static final String ASSIGNMENT_PREFIX = "assignment";

    @IdPrefix
    @Builder.Default
    private String idPrefix = ASSIGNMENT_PREFIX;

    @Version
    private long version;

    @Id
    @GeneratedValue(strategy = USE_ATTRIBUTES)
    private String id;

    @IdAttribute
    private String experimentId;

    @IdAttribute(order = 1)
    private String clientId;

    private String bucketKey;

    private long experimentConfigVersion;

    private Map<String, Object> extraInfo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Generate id of db from assignmentId.
     *
     * @param assignmentId the assignmentId of the assignment
     */
    public static String idFrom(Assignment.AssignmentId assignmentId) {
        return ASSIGNMENT_PREFIX + "." + assignmentId.experimentId().id() + "." + assignmentId.clientId();
    }

    /**
     * Generate AssignmentDb from assignment.
     *
     * @param assignment the assignment
     */
    public static AssignmentDb from(Assignment assignment) {
        return AssignmentDb.builder()
                .bucketKey(Optional.ofNullable(assignment).map(Assignment::getDescription)
                        .map(Assignment.Description::bucket)
                        .map(Experiment.Bucket::key)
                        .orElse(null))
                .clientId(Optional.ofNullable(assignment).map(Assignment::getId).map(Assignment.AssignmentId::clientId)
                        .orElse(null))
                .experimentId(Optional.ofNullable(assignment).map(Assignment::getId).map(Assignment.AssignmentId::experimentId)
                        .map(Experiment.ExperimentId::id).orElse(null))
                .experimentConfigVersion(Optional.ofNullable(assignment).map(Assignment::getDescription)
                        .map(Assignment.Description::experimentConfigVersion)
                        .orElse(0L))
                .extraInfo(Optional.ofNullable(assignment).map(Assignment::getDescription)
                        .map(Assignment.Description::extraInfo)
                        .orElse(new HashMap<>()))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Update AssignmentDb from assignment.
     *
     * @param description the description
     */
    public AssignmentDb update(Assignment.Description description) {
        this.bucketKey = Optional.ofNullable(description.bucket())
                .map(Experiment.Bucket::key).orElse(null);
        this.experimentConfigVersion = description.experimentConfigVersion();
        this.extraInfo = description.extraInfo();
        return this;
    }

    /**
     * Generate assignment from AssignmentDb.
     *
     * @param experiment the experiment
     */
    public Assignment to(Experiment experiment) {
        return Assignment.builder()
                .id(new Assignment.AssignmentId(new Experiment.ExperimentId(experimentId), clientId))
                .description(Assignment.Description.builder()
                        .extraInfo(extraInfo)
                        .experimentConfigVersion(experimentConfigVersion)
                        .bucket(Optional.ofNullable(bucketKey)
                                .flatMap(key -> experiment.getDescription().buckets().stream()
                                        .filter(bucket -> bucket.key().equals(key))
                                        .findFirst())
                                .orElse(null))
                        .build())
                .build();
    }
}
