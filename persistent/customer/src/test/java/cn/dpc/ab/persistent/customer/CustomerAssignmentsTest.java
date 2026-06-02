package cn.dpc.ab.persistent.customer;

import cn.dpc.ab.domain.core.CustomerId;
import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.persistent.cache.LocalCacheManager;
import cn.dpc.ab.persistent.cache.LocalCacheProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerAssignmentsTest {
    private static final String FEATURE_KEY = "checkout";
    private static final String CLIENT_ID = "client-1";

    @Mock
    private Experiments experiments;

    @Mock
    private Experiment.Assignments assignments;

    private CustomerAssignments customerAssignments;

    @BeforeEach
    void setUp() {
        customerAssignments = new CustomerAssignments(experiments,
                CustomerId.builder()
                        .customerId("customer-1")
                        .clientId(CLIENT_ID)
                        .attributes(Map.of("tier", "free"))
                        .build(),
                new LocalCacheManager(new LocalCacheProperties(), null));
    }

    @Test
    void should_clear_existing_bucket_when_current_filter_no_longer_matches_customer() {
        Experiment experiment = collectingExperimentRequiringPaidTier();
        experiment.setAssignments(assignments);
        Assignment existingAssignment = Assignment.builder()
                .id(new Assignment.AssignmentId(experiment.getId(), CLIENT_ID))
                .description(Assignment.Description.builder()
                        .bucket(Experiment.Bucket.builder()
                                .key("treatment")
                                .config(Map.of("enabled", true))
                                .build())
                        .experimentConfigVersion(0L)
                        .build())
                .build();

        when(experiments.getByFeatureKey(FEATURE_KEY)).thenReturn(Mono.just(experiment));
        when(assignments.get(existingAssignment.getId())).thenReturn(Mono.just(existingAssignment));
        when(assignments.addOrUpdate(any(Assignment.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        customerAssignments.getByFeatureKey(new Feature.FeatureId(FEATURE_KEY))
                .as(StepVerifier::create)
                .assertNext(assignment -> {
                    assertNull(assignment.getDescription().bucket());
                    assertEquals(1L, assignment.getDescription().experimentConfigVersion());
                })
                .verifyComplete();

        ArgumentCaptor<Assignment> assignmentCaptor = ArgumentCaptor.forClass(Assignment.class);
        verify(assignments).addOrUpdate(assignmentCaptor.capture());
        assertNull(assignmentCaptor.getValue().getDescription().bucket());
        assertEquals(1L, assignmentCaptor.getValue().getDescription().experimentConfigVersion());
    }

    private Experiment collectingExperimentRequiringPaidTier() {
        return new Experiment(new Experiment.ExperimentId("experiment-1"),
                Experiment.Description.builder()
                        .name("Checkout experiment")
                        .description("Checkout experiment")
                        .featureKey(FEATURE_KEY)
                        .status(Experiment.Status.builder()
                                .staticStatus(Experiment.Status.StaticStatus.PUBLISHED)
                                .build())
                        .filterCondition(FilterCondition.builder()
                                .attributeConditions(List.of(new FilterCondition.CustomAttributeCondition(
                                        "Tier", "tier", FilterCondition.CustomAttributeCondition.Operator.EQUALS,
                                        "paid", null)))
                                .build())
                        .configVersion(1L)
                        .buckets(List.of(Experiment.Bucket.builder()
                                        .key("control")
                                        .name("Control")
                                        .percentage(50)
                                        .build(),
                                Experiment.Bucket.builder()
                                        .key("treatment")
                                        .name("Treatment")
                                        .config(Map.of("enabled", true))
                                        .percentage(50)
                                        .build()))
                        .build());
    }

}
