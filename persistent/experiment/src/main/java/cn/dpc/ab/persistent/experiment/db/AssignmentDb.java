package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.IdAttribute;
import org.springframework.data.couchbase.core.mapping.id.IdPrefix;

import java.util.Map;
import java.util.Optional;

import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.USE_ATTRIBUTES;

/**
 * AssignmentDb provides for save assignments.
 */
@Document
@Data
@Builder
public class AssignmentDb {
    @IdPrefix
    private static final String ASSIGNMENT_PREFIX = "assignment";

    @Version
    private long version;

    @Id
    @GeneratedValue(strategy = USE_ATTRIBUTES)
    private String id;

    @IdAttribute(order = 0)
    private String experimentId;

    @IdAttribute(order = 1)
    private String clientId;

    private String bucketKey;

    private long experimentConfigVersion;

    private Map<String, Object> extraInfo;

    /**
     * Generate id of db from assignmentId.
     *
     * @param assignmentId the assignmentId of the assignment
     */
    public static String idFrom(Assignment.AssignmentId assignmentId) {
        return ASSIGNMENT_PREFIX + "." + assignmentId.experimentId() + "." + assignmentId.clientId();
    }

    /**
     * Generate AssignmentDb from assignment.
     *
     * @param assignment the assignment
     */
    public static AssignmentDb from(Assignment assignment) {
        return AssignmentDb.builder()
                .bucketKey(assignment.getBucket().key())
                .clientId(assignment.getId().clientId())
                .experimentId(assignment.getId().experimentId().id())
                .experimentConfigVersion(assignment.getExperimentConfigVersion())
                .extraInfo(assignment.getExtraInfo())
                .build();
    }

    /**
     * Generate assignment from AssignmentDb.
     *
     * @param experiment the experiment
     */
    public Assignment to(Experiment experiment) {
        return Assignment.builder()
                .id(new Assignment.AssignmentId(new Experiment.ExperimentId(experimentId), clientId))
                .extraInfo(extraInfo)
                .experimentConfigVersion(experimentConfigVersion)
                .bucket(Optional.ofNullable(bucketKey)
                        .flatMap(key -> experiment.getDescription().buckets().stream()
                                .filter(bucket -> bucket.key().equals(key))
                                .findFirst())
                        .orElse(null))
                .build();
    }
}
