package cn.dpc.ab.persistent.couchbase;

import com.couchbase.client.java.ReactiveCluster;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CouchbaseConfiguration.
 */
@Configuration
public class CouchbaseConfiguration {

    @Bean
    public ReactiveCluster couchbaseReactiveCluster(CouchbaseProperties properties) {
        return ReactiveCluster.connect(properties.getConnectionString(),
                properties.getUsername(), properties.getPassword());
    }

}
