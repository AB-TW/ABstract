package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Assignments;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.persistent.experiment.db.AssignmentDb;
import cn.dpc.ab.persistent.experiment.db.AssignmentDbRepository;
import cn.dpc.ab.persistent.experiment.db.ExperimentDb;
import cn.dpc.ab.persistent.experiment.db.ExperimentDbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * AssignmentsImplTest represents a test for AssignmentImpl.
 */
@ExtendWith(MockitoExtension.class)
public class AssignmentsImplTest {
    @Mock
    AssignmentDbRepository repository;

    @Mock
    ExperimentDbRepository experimentDbRepository;

    Assignments assignments;

    @BeforeEach
    void setUp() {
        assignments = new AssignmentsImpl(repository, experimentDbRepository);
    }

    @Test
    void should_update_success() {
        AssignmentDb assignmentDb = AssignmentTestUtil.createAssignmentDb(
                "123123", "234234", 111111, "oldKey");
        ExperimentDb experimentDb = ExperimentTestUtil.createExperimentDb(
                "234234", "experiment1");
        experimentDb.setBuckets(List.of(ExperimentDb.BucketDb.builder()
                .key("bucketKey").percentage(100).build()));
        when(repository.save(any(AssignmentDb.class))).thenReturn(Mono.empty());
        when(repository.findById(any(String.class))).thenReturn(Mono.just(assignmentDb));
        when(experimentDbRepository.findById(any(String.class))).thenReturn(Mono.just(experimentDb));

        assignments.updateBucketKey(new Assignment.AssignmentId(
                new Experiment.ExperimentId("234234"), "123123"),
                        "bucketKey")
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void should_update_return_error_when_find_experiment_is_empty() {
        ExperimentDb experimentDb = ExperimentTestUtil.createExperimentDb(
                "234234", "experiment1");
        experimentDb.setBuckets(List.of(ExperimentDb.BucketDb.builder()
                .key("bucketKey").build()));
        when(experimentDbRepository.findById(any(String.class))).thenReturn(Mono.empty());

        assignments.updateBucketKey(new Assignment.AssignmentId(
                                new Experiment.ExperimentId("234234"), "123123"),
                        "bucketKey")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void should_update_return_error_when_find_assignment_is_empty() {
        ExperimentDb experimentDb = ExperimentTestUtil.createExperimentDb(
                "234234", "experiment1");
        experimentDb.setBuckets(List.of(ExperimentDb.BucketDb.builder()
                .key("bucketKey").build()));
        when(experimentDbRepository.findById(any(String.class))).thenReturn(Mono.just(experimentDb));

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
}
