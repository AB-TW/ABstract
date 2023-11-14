package cn.dpc.ab.api.experiment;

import cn.dpc.ab.api.dto.PageResponse;
import cn.dpc.ab.api.experiment.dto.ExperimentPageRequest;
import cn.dpc.ab.api.experiment.dto.ExperimentRequest;
import cn.dpc.ab.api.experiment.dto.ExperimentResponse;
import cn.dpc.ab.domain.exception.RecordNotFoundException;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * experiment api.
 */
@RestController
@RequestMapping("/experiments")
@RequiredArgsConstructor
public class ExperimentApi {

    private final Experiments experiments;

    @PostMapping
    Mono<ExperimentResponse> add(@RequestBody ExperimentRequest experimentRequest) {
        return experiments.add(experimentRequest.to())
                .map(ExperimentResponse::from);
    }

    @GetMapping
    Mono<PageResponse<ExperimentResponse>> getByPage(ExperimentPageRequest experimentPageRequest) {
        Experiments.FilterParams filterParams = experimentPageRequest.filterParams();
        return experiments.subCollection(filterParams, experimentPageRequest.getFrom(), experimentPageRequest.getTo())
                .map(ExperimentResponse::from)
                .collectList().zipWith(experiments.size(filterParams))
                .map(tuple -> PageResponse.<ExperimentResponse>builder()
                        .content(tuple.getT1())
                        .total(tuple.getT2())
                        .page(experimentPageRequest.getPage())
                        .pageSize(experimentPageRequest.getPageSize())
                        .build());
    }

    @GetMapping("/{experimentId}")
    Mono<ExperimentResponse> get(@PathVariable String experimentId) {
        return experiments.getExperiment(Experiment.ExperimentId.builder().id(experimentId).build())
                .map(ExperimentResponse::from)
                .switchIfEmpty(Mono.error(new RecordNotFoundException(experimentId)));
    }

    @PutMapping("/{experimentId}/publish")
    Mono<Void> publish(@PathVariable String experimentId) {
        return experiments.publish(Experiment.ExperimentId.builder().id(experimentId).build());
    }

    @PutMapping("/{experimentId}/pause")
    Mono<Void> pause(@PathVariable String experimentId) {
        return experiments.pause(Experiment.ExperimentId.builder().id(experimentId).build());
    }

    @PutMapping("/{experimentId}/stop")
    Mono<Void> stop(@PathVariable String experimentId) {
        return experiments.stop(Experiment.ExperimentId.builder().id(experimentId).build());
    }

    @PutMapping("/{experimentId}")
    Mono<Void> update(@PathVariable String experimentId, @RequestBody ExperimentRequest experimentRequest) {
        return experiments.update(Experiment.ExperimentId.builder().id(experimentId).build(),
                experimentRequest.to().getDescription());
    }
}
