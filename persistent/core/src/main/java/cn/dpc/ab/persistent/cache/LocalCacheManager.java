package cn.dpc.ab.persistent.cache;

import cn.dpc.ab.persistent.event.FeatureConfigUpdatedEventImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

/**
 * Local cache manager.
 */
@Component
public class LocalCacheManager {
    private final Cache<String, CorePublisher<?>> cache;
    private final ApplicationContext applicationContext;

    public static final String EXPERIMENT_KEY_PREFIX = "Experiment_";
    public static final String FEATURE_CONFIG_KEY_PREFIX = "FeatureConfig_";

    /**
     * Instantiates a new Local cache manager.
     *
     * @param localCacheProperties local cache properties
     */
    public LocalCacheManager(LocalCacheProperties localCacheProperties, ApplicationContext applicationContext) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(localCacheProperties.getMaxCacheSize())
                .expireAfterWrite(localCacheProperties.getExpireAfterWrite())
                .build();
        this.applicationContext = applicationContext;
    }


    /**
     * Cache flux.
     *
     * @param key      key
     * @param supplier supplier
     * @return flux
     */
    public <T> Flux<T> cacheFlux(String key, Supplier<Flux<T>> supplier) {
        return ((Flux<T>) cache.get(key, k -> supplier.get().cache()))
                .onErrorResume(error -> {
                    cache.invalidate(key);
                    return Flux.error(error);
                });
    }

    /**
     * Cache mono.
     *
     * @param key      key
     * @param supplier supplier
     * @return mono
     */
    public <T> Mono<T> cacheMono(String key, Supplier<Mono<T>> supplier) {
        return ((Mono<T>) cache.get(key, k -> supplier.get().cache()))
                .onErrorResume(error -> {
                    cache.invalidate(key);
                    return Mono.error(error);
                });
    }

    /**
     * Clear cache.
     *
     * @param key key
     * @return void
     */
    public Mono<Void> clearCache(String key) {
        return Mono.fromRunnable(() -> cache.invalidate(key))
                .doOnSuccess(item -> {
                    if (key.startsWith(EXPERIMENT_KEY_PREFIX) || key.startsWith(FEATURE_CONFIG_KEY_PREFIX)) {
                        applicationContext.publishEvent(
                                new FeatureConfigUpdatedEventImpl(key.replace(EXPERIMENT_KEY_PREFIX, "")
                                        .replace(FEATURE_CONFIG_KEY_PREFIX, "")));
                    }
                }).then();
    }
}
