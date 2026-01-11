package cn.dpc.ab.persistent.r2dbc;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Base test class for R2DBC tests using PostgreSQL Testcontainers with Flyway migrations.
 * Extend this class to get automatic database setup with Flyway migrations.
 */
@Testcontainers
public abstract class PostgresTestcontainerBase {

    private static PostgreSQLContainer<?> container;

    public static String r2dbcUrl;
    public static String jdbcUrl;
    public static String username;
    public static String password;

    /**
     * Get the PostgreSQL container, starting it if necessary.
     * This method uses lazy initialization to ensure the container is ready
     * before the Spring context tries to connect to it.
     */
    public static synchronized PostgreSQLContainer<?> getPostgresContainer() {
        if (container == null || !container.isRunning()) {
            startDatabase();
        }
        return container;
    }

    private static void startDatabase() {
        if (container != null && container.isRunning()) {
            return;
        }

        container = new PostgreSQLContainer<>(
                DockerImageName.parse("postgres:16-alpine")
        )
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);

        container.start();

        // Wait for container to be ready
        container.waitingFor(org.testcontainers.containers.wait.strategy.Wait.forListeningPort());

        // Wait until we can actually connect to the database
        waitForDatabase();

        r2dbcUrl = "r2dbc:postgresql://" + container.getHost() + ":"
                + container.getMappedPort(5432) + "/testdb";
        jdbcUrl = "jdbc:postgresql://" + container.getHost() + ":"
                + container.getMappedPort(5432) + "/testdb";
        username = container.getUsername();
        password = container.getPassword();

        // Run Flyway migrations after container is ready
        runFlywayMigrations();
    }

    /**
     * Wait for the database to be ready to accept connections.
     */
    private static void waitForDatabase() {
        int maxAttempts = 30;
        int attempt = 0;
        while (attempt < maxAttempts) {
            try {
                String url = container.getJdbcUrl();
                String user = container.getUsername();
                String password = container.getPassword();

                try (Connection conn = DriverManager.getConnection(url, user, password)) {
                    if (conn.isValid(5)) {
                        return;
                    }
                }
            } catch (Exception e) {
                attempt++;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for database", ie);
                }
            }
        }
        throw new RuntimeException("Database did not become ready after " + maxAttempts + " attempts");
    }

    @AfterAll
    static synchronized void stopDatabase() {
        // Don't stop the container - reuse it for all tests
        // container.stop();
    }

    /**
     * Run Flyway migrations on the test database.
     */
    protected static void runFlywayMigrations() {
        Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, username, password)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();
    }
}
