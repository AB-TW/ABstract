package cn.dpc.ab.persistent.couchbase;

import com.couchbase.client.java.ReactiveCluster;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CouchbaseConfiguration.
 */
@Configuration
public class CouchbaseConfiguration {

    @Value("${spring.couchbase.connection-string:localhost}")
    private String connectionString;

    @Value("${spring.couchbase.username:}")
    private String username;

    @Value("${spring.couchbase.password:}")
    private String password;

    @Bean
    public ReactiveCluster couchbaseReactiveCluster() {
        return ReactiveCluster.connect(connectionString, username, password);
    }

}
