package cn.dpc.ab.api.experiment;

import cn.dpc.ab.api.dto.PageResponse;
import cn.dpc.ab.api.experiment.dto.ExperimentPageRequest;
import cn.dpc.ab.api.experiment.dto.ExperimentRequest;
import cn.dpc.ab.api.experiment.dto.ExperimentResponse;
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

    @PostMapping("/add")
    Mono<ExperimentResponse> add(@RequestBody ExperimentRequest experimentRequest) {
        return experiments.add(experimentRequest.to())
                .map(ExperimentResponse::from);
    }

    @GetMapping
    Mono<PageResponse<ExperimentResponse>> getByPage(@RequestBody ExperimentPageRequest experimentPageRequest) {
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
}
