package cn.dpc.ab.api.experiment;

import cn.dpc.ab.api.experiment.dto.ExperimentRequest;
import cn.dpc.ab.api.experiment.dto.ExperimentResponse;
import cn.dpc.ab.persistent.experiment.db.ExperimentDb;
import cn.dpc.ab.persistent.experiment.db.ExperimentDbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * experiment api.
 */
@RestController
@RequestMapping("/experiment")
@RequiredArgsConstructor
public class ExperimentApi {

    private ExperimentDbRepository experimentDbRepository;

    @PostMapping
    Mono<ExperimentResponse> add(@RequestBody ExperimentRequest experimentRequest) {
        return experimentDbRepository.save(ExperimentDb.from(experimentRequest.to()))
                .map(ExperimentResponse::from);
    }
}
