package cn.dpc.ab.persistent.customer;

import cn.dpc.ab.domain.core.CustomerId;
import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.persistent.cache.LocalCacheManager;
import cn.dpc.ab.persistent.cache.LocalCacheProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerAssignmentsTest {

    @Mock
    private Experiments experiments;

    @Mock
    private Experiment.Assignments assignments;

    @Test
    void should_clear_existing_bucket_when_customer_no_longer_matches_filter_condition() {
        CustomerId customerId = CustomerId.builder()
                .customerId("customer-1")
                .clientId("client-1")
                .attributes(Map.of("tier", "free"))
                .build();
        Experiment experiment = generateCollectingExperiment();
        experiment.setAssignments(assignments);
        Assignment existingAssignment = Assignment.builder()
                .id(new Assignment.AssignmentId(experiment.getId(), customerId.clientId()))
                .description(Assignment.Description.builder()
                        .bucket(experiment.getDescription().buckets().getFirst())
                        .experimentConfigVersion(experiment.getDescription().configVersion())
                        .extraInfo(Map.of("customerIds", new ArrayList<>(List.of(customerId.customerId()))))
                        .build())
                .build();

        when(experiments.getByFeatureKey("feature-1")).thenReturn(Mono.just(experiment));
        when(assignments.get(any(Assignment.AssignmentId.class))).thenReturn(Mono.just(existingAssignment));
        when(assignments.addOrUpdate(any(Assignment.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        new CustomerAssignments(experiments, customerId, new LocalCacheManager(new LocalCacheProperties(), null))
                .getByFeatureKey(new Feature.FeatureId("feature-1"))
                .as(StepVerifier::create)
                .expectNextMatches(assignment -> assignment.getDescription().bucket() == null)
                .verifyComplete();
    }

    private Experiment generateCollectingExperiment() {
        return new Experiment(Experiment.ExperimentId.builder()
                .id("experiment-1")
                .build(),
                Experiment.Description.builder()
                        .name("name")
                        .description("description")
                        .status(Experiment.Status.builder()
                                .staticStatus(Experiment.Status.StaticStatus.PUBLISHED)
                                .build())
                        .featureKey("feature-1")
                        .filterCondition(FilterCondition.builder()
                                .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                                .attributeConditions(List.of(new FilterCondition.CustomAttributeCondition(
                                        "tier",
                                        "tier",
                                        FilterCondition.CustomAttributeCondition.Operator.EQUALS,
                                        "premium",
                                        List.of())))
                                .build())
                        .configVersion(3L)
                        .buckets(List.of(Experiment.Bucket.builder()
                                        .key("treatment")
                                        .name("Treatment")
                                        .config(Map.of("enabled", true))
                                        .percentage(100)
                                        .build()))
                        .build());
    }
}
