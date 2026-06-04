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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomerAssignmentsTest {

    @Test
    void should_clear_existing_bucket_when_customer_no_longer_matches_filter_condition() {
        Feature.FeatureId featureKey = new Feature.FeatureId("checkout");
        CustomerId customerId = new CustomerId("customer-1", "client-1", Map.of("plan", "free"));
        InMemoryAssignments assignments = new InMemoryAssignments(Assignment.builder()
                .id(new Assignment.AssignmentId(new Experiment.ExperimentId("experiment-1"), customerId.clientId()))
                .description(Assignment.Description.builder()
                        .bucket(Experiment.Bucket.builder()
                                .key("treatment")
                                .config(Map.of("enabled", true))
                                .percentage(100)
                                .build())
                        .experimentConfigVersion(1L)
                        .extraInfo(Map.of("customerIds", Set.of(customerId.customerId())))
                        .build())
                .build());
        Experiment experiment = collectingExperiment(featureKey.featureKey(), assignments);
        CustomerAssignments customerAssignments = new CustomerAssignments(new SingleExperimentStore(experiment),
                customerId, new LocalCacheManager(new LocalCacheProperties(), null));

        customerAssignments.getByFeatureKey(featureKey)
                .as(StepVerifier::create)
                .assertNext(assignment -> assertNull(assignment.getDescription().bucket()))
                .verifyComplete();

        assertNull(assignments.updated.getDescription().bucket());
        assertEquals(Set.of(customerId.customerId()), assignments.updated.getDescription().extraInfo().get("customerIds"));
    }

    @Test
    void should_assign_bucket_when_customer_with_empty_assignment_matches_filter_condition_again() {
        Feature.FeatureId featureKey = new Feature.FeatureId("checkout");
        CustomerId customerId = new CustomerId("customer-1", "client-1", Map.of("plan", "pro"));
        InMemoryAssignments assignments = new InMemoryAssignments(Assignment.builder()
                .id(new Assignment.AssignmentId(new Experiment.ExperimentId("experiment-1"), customerId.clientId()))
                .description(Assignment.Description.builder()
                        .bucket(null)
                        .experimentConfigVersion(1L)
                        .extraInfo(Map.of("customerIds", Set.of(customerId.customerId())))
                        .build())
                .build());
        Experiment experiment = collectingExperiment(featureKey.featureKey(), assignments);
        CustomerAssignments customerAssignments = new CustomerAssignments(new SingleExperimentStore(experiment),
                customerId, new LocalCacheManager(new LocalCacheProperties(), null));

        customerAssignments.getByFeatureKey(featureKey)
                .as(StepVerifier::create)
                .assertNext(assignment -> assertEquals("treatment", assignment.getDescription().bucket().key()))
                .verifyComplete();

        assertEquals("treatment", assignments.updated.getDescription().bucket().key());
    }

    @Test
    void should_keep_existing_bucket_when_customer_still_matches_filter_condition() {
        Feature.FeatureId featureKey = new Feature.FeatureId("checkout");
        CustomerId customerId = new CustomerId("customer-1", "client-1", Map.of("plan", "pro"));
        InMemoryAssignments assignments = new InMemoryAssignments(Assignment.builder()
                .id(new Assignment.AssignmentId(new Experiment.ExperimentId("experiment-1"), customerId.clientId()))
                .description(Assignment.Description.builder()
                        .bucket(Experiment.Bucket.builder()
                                .key("treatment")
                                .config(Map.of("enabled", true))
                                .percentage(100)
                                .build())
                        .experimentConfigVersion(1L)
                        .extraInfo(Map.of("customerIds", Set.of(customerId.customerId())))
                        .build())
                .build());
        Experiment experiment = collectingExperiment(featureKey.featureKey(), assignments);
        CustomerAssignments customerAssignments = new CustomerAssignments(new SingleExperimentStore(experiment),
                customerId, new LocalCacheManager(new LocalCacheProperties(), null));

        customerAssignments.getByFeatureKey(featureKey)
                .as(StepVerifier::create)
                .assertNext(assignment -> assertEquals("treatment", assignment.getDescription().bucket().key()))
                .verifyComplete();

        assertNull(assignments.updated);
    }

    private static Experiment collectingExperiment(String featureKey, Experiment.Assignments assignments) {
        Experiment experiment = new Experiment(new Experiment.ExperimentId("experiment-1"),
                Experiment.Description.builder()
                        .name("checkout experiment")
                        .description("checkout experiment")
                        .featureKey(featureKey)
                        .status(Experiment.Status.builder()
                                .staticStatus(Experiment.Status.StaticStatus.PUBLISHED)
                                .build())
                        .filterCondition(FilterCondition.builder()
                                .attributeConditions(List.of(new FilterCondition.CustomAttributeCondition(
                                        "Plan", "plan", FilterCondition.CustomAttributeCondition.Operator.EQUALS,
                                        "pro", null)))
                                .build())
                        .configVersion(1L)
                        .buckets(List.of(Experiment.Bucket.builder()
                                .key("treatment")
                                .config(Map.of("enabled", true))
                                .percentage(100)
                                .build()))
                        .build());
        experiment.setAssignments(assignments);
        return experiment;
    }

    private static class InMemoryAssignments implements Experiment.Assignments {
        private final Assignment existing;
        private Assignment updated;

        InMemoryAssignments(Assignment existing) {
            this.existing = existing;
        }

        @Override
        public Mono<Void> updateBucketKey(Assignment.AssignmentId assignmentId, String bucketKey) {
            return Mono.empty();
        }

        @Override
        public Mono<Assignment> add(Assignment assignment) {
            return Mono.just(assignment);
        }

        @Override
        public Mono<Assignment> get(Assignment.AssignmentId assignmentId) {
            return Mono.just(existing);
        }

        @Override
        public Mono<Assignment> update(Assignment assignment) {
            updated = assignment;
            return Mono.just(assignment);
        }

        @Override
        public Mono<? extends Assignment> addOrUpdate(Assignment assignment) {
            updated = assignment;
            return Mono.just(assignment);
        }

        @Override
        public Flux<Experiment.BucketStaticResult> statisticBucketCount() {
            return Flux.empty();
        }
    }

    private record SingleExperimentStore(Experiment experiment) implements Experiments {
        @Override
        public Flux<Experiment> getAll(FilterParams params) {
            return Flux.just(experiment);
        }

        @Override
        public Flux<Experiment> subCollection(FilterParams params, long from, long to) {
            return Flux.just(experiment);
        }

        @Override
        public Mono<Long> size(FilterParams params) {
            return Mono.just(1L);
        }

        @Override
        public Mono<Experiment> add(Experiment experiment) {
            return Mono.just(experiment);
        }

        @Override
        public Mono<Experiment> get(Experiment.ExperimentId id) {
            return Mono.just(experiment);
        }

        @Override
        public Mono<Void> update(Experiment.ExperimentId id, Experiment.Description description) {
            return Mono.empty();
        }

        @Override
        public Mono<Void> publish(Experiment.ExperimentId id) {
            return Mono.empty();
        }

        @Override
        public Mono<Void> pause(Experiment.ExperimentId id) {
            return Mono.empty();
        }

        @Override
        public Mono<Void> stop(Experiment.ExperimentId id) {
            return Mono.empty();
        }

        @Override
        public Mono<Experiment> getByFeatureKey(String featureKey) {
            return Mono.just(experiment);
        }

        @Override
        public Mono<Void> delete(Experiment.ExperimentId id) {
            return Mono.empty();
        }
    }
}
