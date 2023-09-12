package cn.dpc.ab.persistent.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Local cache properties.
 */
@ConfigurationProperties(prefix = "cache.local")
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocalCacheProperties {
    private int maxCacheSize = 1000;

    private Duration expireAfterWrite = Duration.ofMinutes(5);
}
