package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import cn.dpc.ab.persistent.r2dbc.PostgresTestcontainerBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.List;

import static cn.dpc.ab.persistent.experiment.db.ExperimentTestUtil.createExperimentDb;

/**
 * Test for CustomerExperimentRepository.
 */

@ExtendWith(SpringExtension.class)
@DataR2dbcTest(excludeAutoConfiguration = {
        org.springframework.boot.r2dbc.autoconfigure.R2dbcInitializationAutoConfiguration.class
})
@Import(R2dbcTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerExperimentRepositoryTest extends PostgresTestcontainerBase {

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    private DatabaseClient databaseClient;

    private CustomerExperimentRepository repository;

    @BeforeEach
    void setUp() {
        // Schema is created by Flyway migrations in PostgresTestcontainerBase
        cleanupDatabase(databaseClient, "assignment, experiment");
        repository = new CustomerExperimentRepository(template);
    }

    private void cleanupDatabase(DatabaseClient databaseClient, String tables) {
        try {
            String[] tableArray = tables.split(",");
            for (String table : tableArray) {
                String sql = "DELETE FROM " + table.trim().toLowerCase();
                System.out.println("Executing cleanup: " + sql);
                Long count = databaseClient.sql(sql).fetch().rowsUpdated().block();
                System.out.println("Deleted " + count + " rows from " + table.trim());
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to cleanup database tables: " + e.getMessage());
        }
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
