package cn.dpc.ab.persistent.customer;

import cn.dpc.ab.domain.core.CustomerId;
import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.persistent.cache.LocalCacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * CustomerAssignmentsTest represents tests for CustomerAssignments.
 */
@ExtendWith(MockitoExtension.class)
class CustomerAssignmentsTest {
    private static final String FEATURE_KEY = "feature-a";
    private static final String CLIENT_ID = "client-1";

    @Mock
    private Experiments experiments;

    @Mock
    private Experiment.Assignments assignments;

    @Mock
    private LocalCacheManager localCacheManager;

    private CustomerAssignments customerAssignments;

    @BeforeEach
    void setUp() {
        customerAssignments = new CustomerAssignments(experiments,
                new CustomerId(null, CLIENT_ID, Map.of()), localCacheManager);
    }

    @Test
    void should_reassign_existing_bucket_when_experiment_config_version_changes() {
        Experiment experiment = collectingExperiment(1L);
        Assignment staleAssignment = assignment(experiment, 0L);

        when(localCacheManager.cacheMono(eq(LocalCacheManager.EXPERIMENT_KEY_PREFIX + FEATURE_KEY), any()))
                .thenAnswer(invocation -> ((Supplier<Mono<Experiment>>) invocation.getArgument(1)).get());
        when(experiments.getByFeatureKey(FEATURE_KEY)).thenReturn(Mono.just(experiment));
        when(assignments.get(new Assignment.AssignmentId(experiment.getId(), CLIENT_ID)))
                .thenReturn(Mono.just(staleAssignment));
        when(assignments.addOrUpdate(any(Assignment.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        customerAssignments.getByFeatureKey(Feature.FeatureId.builder().featureKey(FEATURE_KEY).build())
                .as(StepVerifier::create)
                .expectNextMatches(assignment ->
                        assignment.getDescription().experimentConfigVersion() == 1L
                                && assignment.getDescription().bucket() != null)
                .verifyComplete();

        verify(assignments).addOrUpdate(any(Assignment.class));
    }

    @Test
    void should_keep_existing_assignment_when_version_changes_outside_collecting() {
        Experiment experiment = experiment(1L, Experiment.Status.StaticStatus.PAUSED);
        Assignment staleAssignment = assignment(experiment, 0L);

        when(localCacheManager.cacheMono(eq(LocalCacheManager.EXPERIMENT_KEY_PREFIX + FEATURE_KEY), any()))
                .thenAnswer(invocation -> ((Supplier<Mono<Experiment>>) invocation.getArgument(1)).get());
        when(experiments.getByFeatureKey(FEATURE_KEY)).thenReturn(Mono.just(experiment));
        when(assignments.get(new Assignment.AssignmentId(experiment.getId(), CLIENT_ID)))
                .thenReturn(Mono.just(staleAssignment));

        customerAssignments.getByFeatureKey(Feature.FeatureId.builder().featureKey(FEATURE_KEY).build())
                .as(StepVerifier::create)
                .expectNext(staleAssignment)
                .verifyComplete();

        verify(assignments, never()).addOrUpdate(any(Assignment.class));
    }

    @Test
    void should_preserve_assignment_extra_info_when_reassigning() {
        Experiment experiment = collectingExperiment(1L);
        Assignment staleAssignment = assignment(experiment, 0L, Map.of("customerIds", List.of("customer-a")));

        when(localCacheManager.cacheMono(eq(LocalCacheManager.EXPERIMENT_KEY_PREFIX + FEATURE_KEY), any()))
                .thenAnswer(invocation -> ((Supplier<Mono<Experiment>>) invocation.getArgument(1)).get());
        when(experiments.getByFeatureKey(FEATURE_KEY)).thenReturn(Mono.just(experiment));
        when(assignments.get(new Assignment.AssignmentId(experiment.getId(), CLIENT_ID)))
                .thenReturn(Mono.just(staleAssignment));
        when(assignments.addOrUpdate(any(Assignment.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        customerAssignments.getByFeatureKey(Feature.FeatureId.builder().featureKey(FEATURE_KEY).build())
                .as(StepVerifier::create)
                .expectNextMatches(assignment ->
                        assignment.getDescription().experimentConfigVersion() == 1L
                                && assignment.getDescription().extraInfo().equals(staleAssignment.getDescription().extraInfo()))
                .verifyComplete();
    }

    private Experiment collectingExperiment(long configVersion) {
        return experiment(configVersion, Experiment.Status.StaticStatus.PUBLISHED);
    }

    private Experiment experiment(long configVersion, Experiment.Status.StaticStatus status) {
        Experiment experiment = new Experiment(new Experiment.ExperimentId("experiment-1"),
                Experiment.Description.builder()
                        .name("Experiment")
                        .description("Experiment description")
                        .featureKey(FEATURE_KEY)
                        .status(Experiment.Status.builder()
                                .staticStatus(status)
                                .build())
                        .buckets(List.of(
                                Experiment.Bucket.builder().key("a").percentage(100).build()))
                        .configVersion(configVersion)
                        .build());
        experiment.setAssignments(assignments);
        return experiment;
    }

    private Assignment assignment(Experiment experiment, long configVersion) {
        return assignment(experiment, configVersion, null);
    }

    private Assignment assignment(Experiment experiment, long configVersion, Map<String, Object> extraInfo) {
        return Assignment.builder()
                .id(new Assignment.AssignmentId(experiment.getId(), CLIENT_ID))
                .description(Assignment.Description.builder()
                        .bucket(Experiment.Bucket.builder().key("a").percentage(100).build())
                        .experimentConfigVersion(configVersion)
                        .extraInfo(extraInfo)
                        .build())
                .build();
    }
}
