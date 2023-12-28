package cn.dpc.ab.api.experiment.dto;

import cn.dpc.ab.domain.experiment.Assignment;
import lombok.*;

import java.util.Optional;

/**
 * AssignmentResult represents the returned result for Assignments.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AssignmentResult {
    public static final AssignmentResult EMPTY = AssignmentResult.builder().build();
    private String experimentId;
    //    private String experimentVersion;
    private String bucketKey;
    private String clientId;
    private String customerId;

    /**
     * from converts Assignment to AssignmentResult.
     *
     * @param assignment Assignment
     * @return AssignmentResult
     */
    public static AssignmentResult from(Assignment assignment) {
        return AssignmentResult.builder()
                .experimentId(Optional.ofNullable(assignment.getId().experimentId().id()).orElse(null))
                .clientId(assignment.getId().clientId())
                //   .bucketKey(assignment.getExtraInfo())
                .build();
    }
}
