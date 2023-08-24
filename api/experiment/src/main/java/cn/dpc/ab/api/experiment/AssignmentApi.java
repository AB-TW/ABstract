package cn.dpc.ab.api.experiment;

import cn.dpc.ab.api.experiment.dto.AssignmentResult;
import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Assignments;
import cn.dpc.ab.domain.experiment.Experiment;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * AssignmentApi represents the API for assignments.
 */
@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
public class AssignmentApi {
    private final Assignments assignments;

    /**
     * Get assignments by featureKey and experimentId.
     */
    @GetMapping("/{featureKey}/assignment/{experimentId}")
    public Mono<AssignmentResult> getAssignment(@PathVariable("featureKey") String featureKey,
                                                @PathVariable("experimentId") String experimentId,
                                                @RequestHeader(value = "x-customer-id", required = false) String customerId,
                                                @RequestHeader("x-device-id") String clientId) {
        return assignments.getAssignmentById(featureKey, customerId,
                        new Assignment.AssignmentId(new Experiment.ExperimentId(experimentId), clientId))
                .map(AssignmentResult::from);
    }
}
