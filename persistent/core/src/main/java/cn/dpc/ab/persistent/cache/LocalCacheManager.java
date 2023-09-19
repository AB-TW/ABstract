package cn.dpc.ab.persistent.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

/**
 * Local cache manager.
 */
@Component
public class LocalCacheManager {
    private final Cache<String, Flux<?>> cache;

    /**
     * Instantiates a new Local cache manager.
     *
     * @param localCacheProperties local cache properties
     */
    public LocalCacheManager(LocalCacheProperties localCacheProperties) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(localCacheProperties.getMaxCacheSize())
                .expireAfterWrite(localCacheProperties.getExpireAfterWrite())
                .build();
    }


    /**
     * Cache flux.
     *
     * @param key      key
     * @param supplier supplier
     * @return flux
     */
    public Flux<?> cacheFlux(String key, Supplier<Flux<?>> supplier) {
        return cache.get(key, k -> supplier.get().cache())
                .onErrorResume(error -> {
                    cache.invalidate(key);
                    return Flux.error(error);
                });
    }
}
