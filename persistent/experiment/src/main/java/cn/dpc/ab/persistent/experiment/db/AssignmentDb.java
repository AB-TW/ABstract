package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Assignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * AssignmentDb provides for save assignments.
 */
@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDb {

    private String id;

    @QueryIndexed
    private String featureKey;

    @Version
    private long version;

    private long experimentConfigVersion;

    private String bucketKey;

    private String experimentId;

    private String clientId;

    private Set<String> customerIds;

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
                this.experimentConfigVersion,
                Experiment.Bucket.builder().key(this.bucketKey).build(),
                this.customerIds,
                this.extraInfo
        );
    }
}
