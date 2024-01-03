package cn.dpc.ab.persistent.experiment.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static cn.dpc.ab.persistent.experiment.db.AssignmentTestUtil.createAssignmentDb;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest(properties = "logging.level.org.springframework.data.r2dbc=DEBUG")
class AssignmentRepositoryTest {
    @Autowired
    private AssignmentRepository assignmentRepository;

    @BeforeEach
    public void setUp() {
        assignmentRepository.deleteAll().block();
    }

    @Test
    public void should_find_all_success() {
        assignmentRepository.save(createAssignmentDb("experimentId", "clientId")).block();

        assignmentRepository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void should_find_assignmentDb_by_experimentId_and_clientId_success() {
        assignmentRepository.save(createAssignmentDb("experimentId", "clientId")).block();
        assignmentRepository.save(createAssignmentDb("experimentId-1", "clientId-1")).block();

        assignmentRepository.findAssignmentDbByExperimentIdAndClientId("experimentId", "clientId")
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void should_find_assignmentDb_by_experimentId_and_clientId_failed() {
        assignmentRepository.save(createAssignmentDb("experimentId", "clientId")).block();
        assignmentRepository.save(createAssignmentDb("experimentId-1", "clientId-1")).block();

        assignmentRepository.findAssignmentDbByExperimentIdAndClientId("experimentId-2", "clientId")
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }
}
