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
     * Get assignments by clientId and experimentId.
     */
    @GetMapping("/{experimentId}/assignment/{clientId}")
    public Mono<AssignmentResult> getAssignment(@PathVariable("experimentId") String experimentId,
                                                @PathVariable("clientId") String clientId) {
        return assignments.getAssignmentById(new Assignment.AssignmentId(
                new Experiment.ExperimentId(experimentId), clientId)
                )
                .map(AssignmentResult::from);
    }
}
