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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static cn.dpc.ab.domain.core.FilterCondition.CustomAttributeCondition.Operator.EQUALS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerAssignmentsTest {
    private static final Feature.FeatureId FEATURE_KEY = new Feature.FeatureId("feature-key");
    private static final CustomerId CUSTOMER_ID = CustomerId.builder()
            .customerId("")
            .clientId("client-1")
            .attributes(Map.of("country", "cn"))
            .build();

    @Mock
    private Experiments experiments;

    @Mock
    private Experiment.Assignments assignments;

    private CustomerAssignments customerAssignments;

    @BeforeEach
    void setUp() {
        customerAssignments = new CustomerAssignments(experiments, CUSTOMER_ID,
                new LocalCacheManager(new LocalCacheProperties(), null));
    }

    @Test
    void should_reassign_existing_bucket_when_customer_no_longer_matches_filter() {
        Experiment experiment = collectingExperimentWithFilter();
        experiment.setAssignments(assignments);
        Assignment staleBucketAssignment = Assignment.builder()
                .id(new Assignment.AssignmentId(experiment.getId(), CUSTOMER_ID.clientId()))
                .description(Assignment.Description.builder()
                        .bucket(experiment.getDescription().buckets().getFirst())
                        .experimentConfigVersion(0L)
                        .build())
                .build();

        when(experiments.getByFeatureKey(FEATURE_KEY.featureKey()))
                .thenReturn(Mono.just(experiment));
        when(assignments.get(new Assignment.AssignmentId(experiment.getId(), CUSTOMER_ID.clientId())))
                .thenReturn(Mono.just(staleBucketAssignment));
        when(assignments.addOrUpdate(any(Assignment.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        customerAssignments.getByFeatureKey(FEATURE_KEY)
                .as(StepVerifier::create)
                .expectNextMatches(assignment -> assignment.getDescription().bucket() == null)
                .verifyComplete();
    }

    private Experiment collectingExperimentWithFilter() {
        return new Experiment(new Experiment.ExperimentId("experiment-1"),
                Experiment.Description.builder()
                        .name("Experiment")
                        .description("Experiment description")
                        .status(Experiment.Status.builder()
                                .staticStatus(Experiment.Status.StaticStatus.PUBLISHED)
                                .build())
                        .featureKey(FEATURE_KEY.featureKey())
                        .filterCondition(FilterCondition.builder()
                                .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                                .attributeConditions(List.of(new FilterCondition.CustomAttributeCondition(
                                        "Country", "country", EQUALS, "us", null)))
                                .build())
                        .configVersion(1L)
                        .buckets(List.of(
                                Experiment.Bucket.builder().key("variant").name("Variant").percentage(100).build()))
                        .build());
    }
}
