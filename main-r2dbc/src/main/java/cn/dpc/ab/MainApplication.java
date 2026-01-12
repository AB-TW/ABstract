package cn.dpc.ab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application for R2DBC (PostgreSQL/H2).
 * Excludes Couchbase auto-configuration to prevent conflicts with R2DBC.
 */
@SpringBootApplication(excludeName = {
        "org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration",
        "org.springframework.boot.autoconfigure.data.couchbase.CouchbaseDataAutoConfiguration",
        "org.springframework.boot.autoconfigure.data.couchbase.CouchbaseRepositoriesAutoConfiguration"
})
public class MainApplication {

    /**
     * Main method.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}