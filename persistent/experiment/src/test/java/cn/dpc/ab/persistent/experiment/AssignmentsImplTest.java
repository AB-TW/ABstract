package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Assignments;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.persistent.experiment.db.AssignmentDbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * AssignmentsImplTest represents a test for AssignmentImpl.
 */
@ExtendWith(MockitoExtension.class)
public class AssignmentsImplTest {
    @Mock
    AssignmentDbRepository repository;

    Assignments assignments;

    @BeforeEach
    void setUp() {
        assignments = new AssignmentsImpl(repository);
    }

    @Test
    void should_get_success() {
        when(repository.findById(anyString())).thenReturn(Mono.just(AssignmentTestUtil.createAssignmentDb(
                "123123", "234234", 111111
                )));

        assignments.getAssignmentById(new Assignment.AssignmentId(new Experiment.ExperimentId("234234"), "123123"))
                .as(StepVerifier::create)
                .expectNextMatches(assignment -> assignment.getId().clientId().equals("123123")
                && assignment.getId().experimentId().id().equals("234234"))
                .verifyComplete();
    }

    @Test
    void should_get_return_empty_when_id_not_exist() {
        when(repository.findById(anyString())).thenReturn(Mono.empty());

        assignments.getAssignmentById(new Assignment.AssignmentId(new Experiment.ExperimentId("234234"), "123123"))
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }
}
