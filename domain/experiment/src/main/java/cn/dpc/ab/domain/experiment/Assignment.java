package cn.dpc.ab.domain.experiment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Assignment represents an assignment.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Assignment {

    private AssignmentId id;
    private Experiment.Bucket bucket;
    private Map<String, Object> extraInfo;

    /**
     * AssignmentId represents the ID of an assignment.
     */
    public record AssignmentId(Experiment.ExperimentId experimentId, String clientId) {
    }
}
