package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.persistent.utils.JsonUtil;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Map;
import java.util.Optional;

/**
 * ExperimentDb provides a skeleton implementation of the ExperimentDb interface.
 */
@Data
@Table("assignment")
public class AssignmentDb {
    @Id
    private Long id;
    private String experimentId;
    private String clientId;
    private String bucketKey;
    private Long experimentConfigVersion;
    private String extraInfo;


    /**
     * from creates a new AssignmentDb from an Assignment.
     *
     * @param assignment the Assignment to convert
     * @return the AssignmentDb
     */
    public static AssignmentDb from(Assignment assignment) {
        AssignmentDb assignmentDb = new AssignmentDb();
        assignmentDb.setExperimentId(assignment.getId().experimentId().id());
        assignmentDb.setClientId(assignment.getId().clientId());
        assignmentDb.setBucketKey(assignment.getDescription().bucket().key());
        assignmentDb.setExperimentConfigVersion(assignment.getDescription().experimentConfigVersion());
        assignmentDb.setExtraInfo(JsonUtil.toJson(assignment.getDescription().extraInfo()));
        return assignmentDb;
    }


    /**
     * to converts an AssignmentDb to an Assignment.
     *
     * @param experiment the Experiment to use
     * @return the Assignment
     */
    public Assignment to(Experiment experiment) {
        return Assignment.builder()
                .id(new Assignment.AssignmentId(new Experiment.ExperimentId(experimentId), clientId))
                .description(Assignment.Description.builder()
                        .extraInfo(JsonUtil.fromJson(extraInfo, Map.class))
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
