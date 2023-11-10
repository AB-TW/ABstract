package cn.dpc.ab.persistent.experiment.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static cn.dpc.ab.persistent.experiment.db.AssignmentTestUtil.createAssignmentDb;
import static org.springframework.data.domain.Sort.by;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest(properties = "logging.level.org.springframework.data.r2dbc=DEBUG")
class CustomerAssignmentRepositoryTest {
    @Autowired
    R2dbcEntityTemplate template;

    private CustomerAssignmentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new CustomerAssignmentRepository(template);
        template.delete(AssignmentDb.class).all().block();
    }

    @Test
    public void should_find_all_success() {
        String experimentId = "experimentId";
        String clientId = "clientId";
        AssignmentDb assignmentDb = createAssignmentDb(experimentId, clientId);
        template.insert(assignmentDb).block();

        repository.findAll("experimentId")
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        repository.findAll("notExistExperimentId")
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_find_by_experimentId_with_page_success() {
        String experimentId = "experimentId";
        String clientId = "clientId";
        AssignmentDb assignmentDb = createAssignmentDb(experimentId, clientId);
        template.insert(assignmentDb).block();

        PageRequest pageRequest = PageRequest.of(0, 1, by("experimentId").descending());
        repository.findAll("experimentId", pageRequest)
                .as(StepVerifier::create)
                .expectNextMatches(id -> id.getExperimentId().equals(experimentId))
                .verifyComplete();

        repository.findAll("notExistExperimentId", pageRequest)
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_count_by_experimentId_success() {
        String experimentId = "experimentId";
        String clientId = "clientId";
        AssignmentDb assignmentDb = createAssignmentDb(experimentId, clientId);
        template.insert(assignmentDb).block();

        repository.count("experimentId")
                .as(StepVerifier::create)
                .expectNext(1L)
                .verifyComplete();

        repository.count("notExistExperimentId")
                .as(StepVerifier::create)
                .expectNext(0L)
                .verifyComplete();
    }


}
