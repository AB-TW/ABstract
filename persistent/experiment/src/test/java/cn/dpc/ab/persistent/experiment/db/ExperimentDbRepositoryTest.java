package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.persistent.RepositoryTestBase;
import cn.dpc.ab.persistent.experiment.ExperimentTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

class ExperimentDbRepositoryTest extends RepositoryTestBase {
    @Autowired
    ExperimentDbRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll().block();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll().block();
    }

    @Test
    public void should_add_new_experiment_success() {
        ExperimentDb experimentDb = ExperimentTestUtil.createExperimentDb("id", "experiment1");

        repository.save(experimentDb)
                .as(StepVerifier::create)
                .expectNextMatches(experiment -> experiment.getId().equals("id") && experiment.getName().equals("experiment1"))
                .verifyComplete();

        repository.findById("id")
                .as(StepVerifier::create)
                .expectNextMatches(experiment -> experiment.getId().equals("id") && experiment.getName().equals("experiment1"))
                .verifyComplete();
    }

    @Test
    public void should_update_exist_experiment_success() {
        ExperimentDb experimentDb = ExperimentTestUtil.createExperimentDb("id", "experiment1");
        repository.save(experimentDb).block();

        repository.findById("id")
                .map(experiment -> {
                    experiment.setName("experiment2");
                    return experiment;
                })
                .flatMap(repository::save)
                .as(StepVerifier::create)
                .expectNextMatches(experiment -> experiment.getId().equals("id") && experiment.getName().equals("experiment2"))
                .verifyComplete();
    }

}