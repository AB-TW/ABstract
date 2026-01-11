package cn.dpc.ab.persistent.feature.db;

import cn.dpc.ab.domain.feature.Features;
import cn.dpc.ab.persistent.r2dbc.PostgresTestcontainerBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static cn.dpc.ab.persistent.feature.db.FeatureTestUtil.createFeatureDb;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest(excludeAutoConfiguration = {
        org.springframework.boot.r2dbc.autoconfigure.R2dbcInitializationAutoConfiguration.class
})
@Import(R2dbcConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CustomerFeatureRepositoryTest extends PostgresTestcontainerBase {

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    private DatabaseClient databaseClient;

    private CustomerFeatureRepository repository;

    @BeforeEach
    void setUp() {
        // Schema is created by Flyway migrations in PostgresTestcontainerBase
        cleanupDatabase(databaseClient, "feature, feature_config");
        repository = new CustomerFeatureRepository(template);
    }

    private void cleanupDatabase(DatabaseClient databaseClient, String tables) {
        try {
            String[] tableArray = tables.split(",");
            for (String table : tableArray) {
                // Use quoted table names to match migration
                String tableName = table.trim().toLowerCase();
                String sql = "DELETE FROM \"" + tableName + "\"";
                databaseClient.sql(sql).fetch().rowsUpdated().block();
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to cleanup database tables: " + e.getMessage());
        }
    }

    @Test
    void findAll() {
        String featureKey1 = "key1";
        FeatureDb featureDb = createFeatureDb(featureKey1);
        template.insert(featureDb).block();

        String featureKey2 = "key2";
        FeatureDb featureDb2 = createFeatureDb(featureKey2);
        template.insert(featureDb2).block();

        Features.FilterParams params = Features.FilterParams.builder()
                .key("key")
                .build();
        repository.findAll(params)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void should_find_by_filterParams_success() {
        String featureKey1 = "key1";
        FeatureDb featureDb = createFeatureDb(featureKey1);
        template.insert(featureDb).block();

        String featureKey2 = "key2";
        FeatureDb featureDb2 = createFeatureDb(featureKey2);
        template.insert(featureDb2).block();

        Features.FilterParams params = Features.FilterParams.builder()
                .key("key")
                .build();
        repository.findAll(params)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void should_find_by_filterParams_with_page_success() {
        String featureKey1 = "key1";
        FeatureDb featureDb = createFeatureDb(featureKey1);
        template.insert(featureDb).block();

        String featureKey2 = "key2";
        FeatureDb featureDb2 = createFeatureDb(featureKey2);
        template.insert(featureDb2).block();

        Features.FilterParams params = Features.FilterParams.builder()
                .key("key")
                .build();
        repository.findAll(params, PageRequest.of(0, 1))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void should_count_by_filterParams_with_page_success() {
        String featureKey1 = "key1";
        FeatureDb featureDb = createFeatureDb(featureKey1);
        template.insert(featureDb).block();

        String featureKey2 = "key2";
        FeatureDb featureDb2 = createFeatureDb(featureKey2);
        template.insert(featureDb2).block();

        Features.FilterParams params = Features.FilterParams.builder()
                .key("key")
                .build();
        repository.count(params)
                .as(StepVerifier::create)
                .expectNext(2L)
                .verifyComplete();
    }
}
