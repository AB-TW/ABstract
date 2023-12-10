package cn.dpc.ab.api.experiment;

import cn.dpc.ab.api.experiment.dto.AssignmentResult;
import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
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
    private final Experiments experiments;

    /**
     * Get assignments by clientId and experimentId.
     */
    @GetMapping("/{experimentId}/assignment/{clientId}")
    public Mono<AssignmentResult> getAssignment(@PathVariable("experimentId") String experimentId,
                                                @PathVariable("clientId") String clientId) {
        return experiments.getExperiment(Experiment.ExperimentId.builder().id(experimentId).build())
                .flatMap(experiment -> experiment.getAssignments()
                        .get(new Assignment.AssignmentId(new Experiment.ExperimentId(experimentId), clientId))
                        .map(AssignmentResult::from));
    }


    /**
     * Get assignments by clientId and experimentId.
     */
    @PutMapping("/{experimentId}/assignment/{clientId}")
    public Mono<Void> updateAssignment(@PathVariable("experimentId") String experimentId,
                                                   @PathVariable("clientId") String clientId,
                                                   @RequestBody String bucketKey) {
        return experiments.getExperiment(Experiment.ExperimentId.builder().id(experimentId).build())
                .flatMap(experiment -> experiment.getAssignments()
                        .updateBucketKey(new Assignment.AssignmentId(new Experiment.ExperimentId(experimentId), clientId), bucketKey));
    }
}
