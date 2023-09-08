package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * AssignmentDb represents an assignment.
 */
@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDb {

    private String id;

    @Version
    private long version;

    private long experimentVersion;

    private String bucketKey;

    private String experimentId;

    private String clientId;

    private String customerId;

    private Map<String, Object> extraInfo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * toAssignment converts AssignmentDb to Assignment.
     *
     * @return the assignment
     */
    public Assignment toAssignment() {
        return new Assignment(
                new Assignment.AssignmentId(new Experiment.ExperimentId(this.experimentId),
                        this.clientId),
                Experiment.Bucket.builder().key(this.bucketKey).build(),
                this.extraInfo
        );
    }
}