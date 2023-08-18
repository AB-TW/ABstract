package cn.dpc.ab.api.experiment;

import cn.dpc.ab.api.experiment.dto.AssignmentResult;
import cn.dpc.ab.domain.experiment.Assignments;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * AssignmentApi represents the API for customers and .
 */
@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
public class AssignmentApi {
    private final Assignments assignments;

    @GetMapping("/{experimentId}/assignment")
    public Mono<AssignmentResult> getAssignment(@PathVariable("featureKey") String featureKey,
                                                @RequestHeader(value = "x-customer-id", required = false) String customerId,
                                                @RequestHeader("x-device-id") String clientId) {
        return assignments.getAssignmentById(featureKey, customerId, clientId)
                .map(AssignmentResult::from);
    }
}
