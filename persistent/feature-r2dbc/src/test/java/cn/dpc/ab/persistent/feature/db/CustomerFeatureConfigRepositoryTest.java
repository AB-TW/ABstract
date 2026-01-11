package cn.dpc.ab.persistent.feature.db;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.persistent.r2dbc.PostgresTestcontainerBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static cn.dpc.ab.persistent.feature.db.FeatureTestUtil.generateFeatureConfig;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest(excludeAutoConfiguration = {
        org.springframework.boot.r2dbc.autoconfigure.R2dbcInitializationAutoConfiguration.class
})
@Import(R2dbcConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CustomerFeatureConfigRepositoryTest extends PostgresTestcontainerBase {

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    private DatabaseClient databaseClient;

    private CustomerFeatureConfigRepository repository;

    @BeforeEach
    void setUp() {
        // Schema is created by Flyway migrations in PostgresTestcontainerBase
        cleanupDatabase(databaseClient, "feature, feature_config");
        repository = new CustomerFeatureConfigRepository(template);
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
    public void should_find_all_success() {
        String featureKey = "featureKey";
        template.insert(generateFeatureConfig(featureKey)).block();
        template.insert(generateFeatureConfig(featureKey)).block();

        repository.findAll(featureKey, Feature.Configs.FilterParams.builder()
                        .status(List.of(FeatureConfig.Status.DynamicStatus.RUNNING))
                        .title("tit")
                        .build())
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void should_find_all_with_page_success() {
        String featureKey = "featureKey";
        template.insert(generateFeatureConfig(featureKey)).block();
        template.insert(generateFeatureConfig(featureKey)).block();
        FeatureConfigDb item3 = generateFeatureConfig(featureKey);
        item3.setTitle("differentTitle");
        template.insert(item3).block();

        repository.findAll(featureKey, Feature.Configs.FilterParams.builder()
                        .title("tit")
                        .build(), PageRequest.of(0, 10))
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void should_count_success() {
        String featureKey = "featureKey";
        template.insert(generateFeatureConfig(featureKey)).block();
        template.insert(generateFeatureConfig(featureKey)).block();

        repository.count(featureKey, Feature.Configs.FilterParams.builder()
                        .status(List.of(FeatureConfig.Status.DynamicStatus.RUNNING))
                        .build())
                .as(StepVerifier::create)
                .expectNext(2L)
                .verifyComplete();
    }
}
