package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.persistent.RepositoryTestBase;
import cn.dpc.ab.persistent.experiment.AssignmentTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

/**
 * AssignmentDbRepositoryTest represents a test of db repository of assignment.
 */
public class AssignmentDbRepositoryTest extends RepositoryTestBase {
    @Autowired
    AssignmentDbRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll().block();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll().block();
    }

    @Test
    public void should_add_new_assignment_success() {
        AssignmentDb assignmentDb = AssignmentTestUtil.createAssignmentDb("123123", "234234", 111111, "bucketKey");

        repository.save(assignmentDb)
                .as(StepVerifier::create)
                .expectNextMatches(assignment -> assignment.getClientId().equals("123123")
                        && assignment.getExperimentId().equals("234234")
                        && assignment.getExperimentConfigVersion() == 111111)
                .verifyComplete();

        repository.findById("assignment.234234.123123")
                .as(StepVerifier::create)
                .expectNextMatches(assignment -> assignment.getClientId().equals("123123")
                        && assignment.getExperimentId().equals("234234")
                        && assignment.getExperimentConfigVersion() == 111111)
                .verifyComplete();
    }
}
