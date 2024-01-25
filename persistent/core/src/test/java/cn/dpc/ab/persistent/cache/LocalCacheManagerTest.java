package cn.dpc.ab.persistent.cache;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalCacheManagerTest {
    private final LocalCacheManager localCacheManager = new LocalCacheManager(new LocalCacheProperties(), null);

    @Test
    void should_cacheFlux_success() {
        Flux<LocalDateTime> flux = Flux.interval(Duration.ofMillis(100))
                .map(i -> LocalDateTime.now())
                .take(10);
        String key = "testKey";
        LocalDateTime last = localCacheManager.cacheFlux(key, () -> flux)
                .blockLast();

        LocalDateTime last2 = localCacheManager.cacheFlux(key, () -> flux)
                .blockLast();

        assertEquals(last, last2);
    }
}