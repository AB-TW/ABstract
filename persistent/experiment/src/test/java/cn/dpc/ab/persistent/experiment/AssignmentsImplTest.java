package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.persistent.experiment.db.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * AssignmentsImplTest represents a test for AssignmentImpl.
 */
@ExtendWith(MockitoExtension.class)
public class AssignmentsImplTest {
    @Mock
    AssignmentDbRepository repository;
    @Mock
    AssignmentStatisticsRepository statisticsRepository;
    @Mock
    Experiment.Assignments assignments;
    Experiment experiment;
    AssignmentDb assignmentDb;
    ExperimentDb experimentDb;

    @BeforeEach
    void setUp() {
        assignmentDb = AssignmentTestUtil.createAssignmentDb(
                "123123", "234234", 111111, "oldKey");
        experimentDb = ExperimentTestUtil.createExperimentDb(
                "234234", "experiment1");
        experiment = ExperimentTestUtil.generateExperiment("234234");
        assignments = new AssignmentsImpl(repository, statisticsRepository, experiment);
    }

    @Test
    void should_update_success() {
        experimentDb.setBuckets(List.of(ExperimentDb.BucketDb.builder()
                .key("key1").percentage(100).build()));
        when(repository.save(any(AssignmentDb.class))).thenReturn(Mono.empty());
        when(repository.findById(any(String.class))).thenReturn(Mono.just(assignmentDb));

        assignments.updateBucketKey(new Assignment.AssignmentId(
                                new Experiment.ExperimentId("234234"), "123123"),
                        "key1")
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void should_update_return_error_when_find_experiment_is_empty() {
        experimentDb.setBuckets(List.of(ExperimentDb.BucketDb.builder()
                .key("bucketKey").build()));
        when(repository.findById(any(String.class))).thenReturn(Mono.empty());

        assignments.updateBucketKey(new Assignment.AssignmentId(
                                new Experiment.ExperimentId("234234"), "123123"),
                        "bucketKey")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void should_update_return_error_when_find_assignment_is_empty() {
        experimentDb.setBuckets(List.of(ExperimentDb.BucketDb.builder()
                .key("bucketKey").build()));
        when(repository.findById(any(String.class))).thenReturn(Mono.empty());

        assignments.updateBucketKey(new Assignment.AssignmentId(
                                new Experiment.ExperimentId("234234"), "123123"),
                        "bucketKey")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void should_get_success() {
        when(repository.findById(anyString())).thenReturn(Mono.just(AssignmentTestUtil.createAssignmentDb(
                "123123", "234234", 111111, "bucketKey"
        )));

        assignments.get(new Assignment.AssignmentId(new Experiment.ExperimentId("234234"), "123123"))
                .as(StepVerifier::create)
                .expectNextMatches(assignment -> assignment.getId().clientId().equals("123123")
                        && assignment.getId().experimentId().id().equals("234234"))
                .verifyComplete();
    }

    @Test
    void should_get_return_empty_when_id_not_exist() {
        when(repository.findById(anyString())).thenReturn(Mono.empty());

        assignments.get(new Assignment.AssignmentId(new Experiment.ExperimentId("234234"), "123123"))
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void should_statisticBucketCount_success() {
        when(statisticsRepository.statisticCountByBucketKey(anyString()))
                .thenReturn(Flux.just(new BucketCount("key1", 1L)));

        assignments.statisticBucketCount()
                .collectList()
                .as(StepVerifier::create)
                .expectNextMatches(item ->
                        item.size() == 3
                                && item.stream().anyMatch(bucketCount -> bucketCount.bucket().key() == null && bucketCount.count() == 0L)
                                && item.stream().anyMatch(bucketCount -> Objects.equals(bucketCount.bucket().key(), "key1")
                                && bucketCount.count() == 1L)
                                && item.stream().anyMatch(bucketCount -> Objects.equals(bucketCount.bucket().key(), "key2")
                                && bucketCount.count() == 0L)
                ).verifyComplete();
    }

    @Test
    public void should_statisticBucketCount_success_when_empty_bucket_has_count() {
        when(statisticsRepository.statisticCountByBucketKey(anyString()))
                .thenReturn(Flux.just(new BucketCount("key1", 1L),
                        new BucketCount("key2", 2L),
                        new BucketCount(null, 1L)));

        assignments.statisticBucketCount()
                .collectList()
                .as(StepVerifier::create)
                .expectNextMatches(item ->
                        item.size() == 3
                                && item.stream().anyMatch(bucketCount -> bucketCount.bucket().key() == null
                                && bucketCount.count() == 1L)
                                && item.stream().anyMatch(bucketCount -> Objects.equals(bucketCount.bucket().key(), "key1")
                                && bucketCount.count() == 1L)
                                && item.stream().anyMatch(bucketCount -> Objects.equals(bucketCount.bucket().key(), "key2")
                                && bucketCount.count() == 2L)
                ).verifyComplete();
    }
}
