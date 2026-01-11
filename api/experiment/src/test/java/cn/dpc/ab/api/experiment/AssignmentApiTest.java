package cn.dpc.ab.api.experiment;

import cn.dpc.ab.api.TestConfiguration;
import cn.dpc.ab.api.experiment.dto.AssignmentResult;
import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * AssignmentApiTest represents a test of assignment api.
 */
@ExtendWith(SpringExtension.class)
@WebFluxTest(value = AssignmentApi.class, properties = "spring.main.lazy-initialization=true")
@ContextConfiguration(classes = TestConfiguration.class)
public class AssignmentApiTest {
    @Autowired
    WebTestClient webClient;

    @MockitoBean
    Experiments experiments;

    Experiment.Assignments assignments;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        assignments = mock(Experiment.Assignments.class);
    }

    @Test
    void should_getAssignmentById_return_empty_when_assignments_return_empty() {
        String experimentId = "experimentId";
        Experiment experiment = AssignmentTestUtil.generateExperiment(experimentId);

        when(experiments.get(Mockito.any())).thenReturn(Mono.just(experiment));
        experiment.setAssignments(assignments);
        when(assignments.get(Mockito.any())).thenReturn(Mono.empty());
        String clientId = "clientId";

        webClient.get()
                .uri((builder) -> builder.path("/assignments/" + experimentId
                                + "/assignment/" + clientId).build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AssignmentResult.class)
                .hasSize(0);
    }

    @Test
    void should_getAssignmentById_return_success_when_assignments_return_success() {
        String experimentId = "experimentId";
        Experiment experiment = AssignmentTestUtil.generateExperiment(experimentId);

        when(experiments.get(Mockito.any())).thenReturn(Mono.just(experiment));
        experiment.setAssignments(assignments);
        String clientId = "clientId";
        String bucketKey = "bucketKey";
        long experimentConfigVersion = 123456;
        when(assignments.get(Mockito.any()))
                .thenReturn(Mono.just(AssignmentTestUtil.createAssignment(
                        clientId, experimentId, bucketKey, experimentConfigVersion)));

        webClient.get()
                .uri((builder) -> builder.path("/assignments/" + experimentId
                                + "/assignment/" + clientId).build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AssignmentResult.class)
                .hasSize(1);
    }

    @Test
    void should_add_assignment_success() {
        String experimentId = "experimentId";
        Experiment experiment = AssignmentTestUtil.generateExperiment(experimentId);

        when(experiments.get(Mockito.any())).thenReturn(Mono.just(experiment));
        experiment.setAssignments(assignments);
        when(assignments.updateBucketKey(any(Assignment.AssignmentId.class), any(String.class)))
                .thenReturn(Mono.empty());
        String clientId = "clientId";
        String bucketKey = "bucketKey";
        webClient.put()
                .uri((builder) -> builder.path("/assignments/" + experimentId
                        + "/assignment/" + clientId).build())
                .body(Mono.just(bucketKey), String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AssignmentResult.class)
                .hasSize(0);
    }
}
