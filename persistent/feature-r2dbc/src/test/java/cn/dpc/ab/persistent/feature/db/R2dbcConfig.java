package cn.dpc.ab.persistent.feature.db;

import cn.dpc.ab.persistent.r2dbc.PostgresTestcontainerBase;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * R2DBC test configuration using PostgreSQL Testcontainers with Flyway migrations.
 */
@TestConfiguration
public class R2dbcConfig extends PostgresTestcontainerBase {

    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        return createConnectionFactory();
    }

    private PostgresqlConnectionFactory createConnectionFactory() {
        var container = getPostgresContainer();
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host(container.getHost())
                        .port(container.getMappedPort(5432))
                        .database(container.getDatabaseName())
                        .username(container.getUsername())
                        .password(container.getPassword())
                        .build()
        );
    }
}
