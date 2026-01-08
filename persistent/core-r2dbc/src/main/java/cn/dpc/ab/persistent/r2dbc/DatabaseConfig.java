package cn.dpc.ab.persistent.r2dbc;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DatabaseConfig {

    @Value("${spring.flyway.url}")
    private String flywayUrl;

    @Value("${spring.flyway.locations:classpath:db/migration}")
    private String flywayLocations;

    @Value("${spring.r2dbc.url}")
    private String r2dbcUrl;

    @Value("${spring.r2dbc.username}")
    private String username;

    @Value("${spring.r2dbc.password}")
    private String password;

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        String url = extractJdbcUrl(r2dbcUrl);
        return Flyway.configure()
                .dataSource(url, username, password)
                .locations(flywayLocations.split(","))
                .baselineOnMigrate(true)
                .load();
    }

    private String extractJdbcUrl(String r2dbcUrl) {
        // Convert r2dbc:postgresql:// to jdbc:postgresql://
        return r2dbcUrl.replace("r2dbc:", "jdbc:");
    }
}
