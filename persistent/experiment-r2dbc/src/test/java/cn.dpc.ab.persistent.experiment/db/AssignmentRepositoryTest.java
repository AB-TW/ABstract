package cn.dpc.ab.persistent.experiment.db;

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

import static cn.dpc.ab.persistent.experiment.db.AssignmentTestUtil.createAssignmentDb;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest(excludeAutoConfiguration = {
        org.springframework.boot.r2dbc.autoconfigure.R2dbcInitializationAutoConfiguration.class
})
@Import(R2dbcTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AssignmentRepositoryTest extends PostgresTestcontainerBase {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    public void setUp() {
        // Schema is created by Flyway migrations in PostgresTestcontainerBase
        cleanupDatabase(databaseClient, "assignment, experiment");
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
