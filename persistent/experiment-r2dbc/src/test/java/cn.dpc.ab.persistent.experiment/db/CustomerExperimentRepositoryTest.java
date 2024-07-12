package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.List;

import static cn.dpc.ab.persistent.experiment.db.ExperimentTestUtil.createExperimentDb;

/**
 * Test for CustomerExperimentRepository.
 */

@ExtendWith(SpringExtension.class)
@DataR2dbcTest(properties = "logging.level.org.springframework.data.r2dbc=DEBUG")

public class CustomerExperimentRepositoryTest {
    @Autowired
    R2dbcEntityTemplate template;

    private CustomerExperimentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new CustomerExperimentRepository(template);
        template.delete(ExperimentDb.class).all().block();
    }

    @Test
    public void should_find_all_success() {
        String key = "key";
        template.insert(createExperimentDb(key, "111")).block();
        template.insert(createExperimentDb(key, "222")).block();

        repository.findAll(Experiments.FilterParams.builder()
                        .featureKey(key)
                        .status(List.of(Experiment.Status.DynamicStatus.COLLECTING))
                .build())
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void should_find_all_with_page_success() {
        String key = "key";
        template.insert(createExperimentDb(key, "111")).block();
        template.insert(createExperimentDb(key, "222")).block();

        ExperimentDb item = createExperimentDb(key, "333");
        item.setName("name");
        template.insert(item).block();

        PageRequest pageRequest = PageRequest.of(0, 1).withSort(Sort.by("updatedAt").descending());

        repository.findAll(Experiments.FilterParams.builder()
                        .featureKey("key")
                        .status(List.of(Experiment.Status.DynamicStatus.COLLECTING))
                        .build(), pageRequest)
                .as(StepVerifier::create)
                .expectNextMatches(id -> id.getName().equals("name"))
                .verifyComplete();
    }

    @Test
    public void should_count_success() {
        String key = "key";
        template.insert(createExperimentDb(key, "111")).block();
        template.insert(createExperimentDb(key, "222")).block();
        ExperimentDb item3 = createExperimentDb(key, "333");
        item3.setName("item3");
        template.insert(item3).block();

        repository.count(Experiments.FilterParams.builder()
                        .status(List.of(Experiment.Status.DynamicStatus.COLLECTING))
                        .featureKey(key)
                .build())
                .as(StepVerifier::create)
                .expectNext(3L)
                .verifyComplete();

        repository.count(Experiments.FilterParams.builder()
                        .status(List.of(Experiment.Status.DynamicStatus.PAUSED))
                        .featureKey(key)
                .build())
                .as(StepVerifier::create)
                .expectNext(0L)
                .verifyComplete();
    }
}
