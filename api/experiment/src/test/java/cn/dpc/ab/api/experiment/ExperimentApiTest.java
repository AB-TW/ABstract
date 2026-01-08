package cn.dpc.ab.api.experiment;

import cn.dpc.ab.api.TestConfiguration;
import cn.dpc.ab.api.experiment.dto.ExperimentRequest;
import cn.dpc.ab.api.experiment.dto.ExperimentResponse;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import cn.dpc.ab.persistent.experiment.db.ExperimentDb;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(value = ExperimentApi.class, properties = "spring.main.lazy-initialization=true")
@ContextConfiguration(classes = TestConfiguration.class)
class ExperimentApiTest {

    @Autowired
    WebTestClient webClient;

    @MockitoBean
    Experiments experiments;

    @Test
    void add() {
        when(experiments.add(Mockito.any())).thenReturn(Mono.empty());

        ExperimentRequest experimentRequest = ExperimentRequest.builder()
                .filterCondition(null)
                .name("test")
                .featureKey("test")
                .buckets(List.of(Experiment.Bucket.builder().key("test").percentage(100).description("").build()))
                .description("")
                .status(Experiment.Status.builder().staticStatus(Experiment.Status.StaticStatus.PUBLISHED).build())
                .build();

        webClient.post()
                .uri("/experiments")
                .body(Mono.just(experimentRequest), ExperimentRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ExperimentResponse.class)
                .hasSize(0);
    }

    @Test
    void delete() {
        ExperimentDb experimentDb = ExperimentDb.builder()
                .id("123")
                .buckets(List.of(ExperimentDb.BucketDb.builder().key("key").percentage(100).name("test").build()))
                .build();
        Experiment experiment = experimentDb.toExperiment();

        when(experiments.get(Mockito.any())).thenReturn(Mono.just(experiment));

        webClient.delete()
                .uri("/experiments/123")
                .exchange()
                .expectStatus()
                .isOk();
    }
}
