package cn.dpc.ab.persistent.feature.db;

import cn.dpc.ab.persistent.r2dbc.PostgresTestcontainerBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.UUID;

import static cn.dpc.ab.persistent.feature.db.FeatureTestUtil.createFeatureDb;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest(excludeAutoConfiguration = {
        org.springframework.boot.r2dbc.autoconfigure.R2dbcInitializationAutoConfiguration.class
})
@Import(R2dbcConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FeatureRepositoryTest extends PostgresTestcontainerBase {

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    public void setUp() {
        // Schema is created by Flyway migrations in PostgresTestcontainerBase
        cleanupDatabase(databaseClient, "feature");
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
        FeatureDb featureDb = createFeatureDb(UUID.randomUUID().toString());
        featureRepository.save(featureDb).block();

        featureRepository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void should_find_by_featureKey_success() {
        String key = UUID.randomUUID().toString();
        FeatureDb featureDb = createFeatureDb(key);
        featureRepository.save(featureDb).block();

        featureRepository.findByFeatureKey(key)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }
}
