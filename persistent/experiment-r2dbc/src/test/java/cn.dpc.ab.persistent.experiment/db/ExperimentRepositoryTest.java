package cn.dpc.ab.persistent.experiment.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static cn.dpc.ab.persistent.experiment.db.ExperimentTestUtil.createExperimentDb;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest(properties = "logging.level.org.springframework.data.r2dbc=DEBUG")
class ExperimentRepositoryTest {
    @Autowired
    private ExperimentRepository experimentRepository;

    @BeforeEach
    public void setUp() {
        experimentRepository.deleteAll().block();
    }

    @Test
    public void should_find_all_success() {
        experimentRepository.save(createExperimentDb("key", "1213")).block();

        experimentRepository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

}
