package cn.dpc.ab.persistent.customer;

import cn.dpc.ab.domain.core.CustomerId;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.domain.feature.Features;
import cn.dpc.ab.persistent.cache.LocalCacheManager;
import cn.dpc.ab.persistent.cache.LocalCacheProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerFeatureConfigsTest {

    @Mock
    Features features;

    private CustomerFeatureConfigs customerFeatureConfigs;

    private Feature.Configs configs;

    @BeforeEach
    void setUp() {
        customerFeatureConfigs = new CustomerFeatureConfigs(features,
                new CustomerId("customer-id", "clientId", Map.of()),
                new LocalCacheManager(new LocalCacheProperties(), null));

        configs = Mockito.mock(Feature.Configs.class);
        when(features.get(any(Feature.FeatureId.class)))
                .thenReturn(Mono.just(Feature.builder().id(Feature.FeatureId.builder().featureKey("key").build())
                        .configs(configs).build()));
    }

    @Test
    void should_getByFeatureKey_return_empty_when_no_db_record() {
        String featureKey = "key";
        when(configs.all(any()))
                .thenReturn(Flux.empty());

        customerFeatureConfigs.getByFeatureKey(new Feature.FeatureId(featureKey))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void should_getByFeatureKey_success() {
        String featureKey = "key";
        when(configs.all(any()))
                .thenReturn(Flux.just(FeatureConfigTestUtil.createFeatureConfig("id1", featureKey,
                                FeatureConfig.Status.StaticStatus.DRAFT, 0),
                        FeatureConfigTestUtil.createFeatureConfig("id2", featureKey,
                                FeatureConfig.Status.StaticStatus.PUBLISHED, 1),
                        FeatureConfigTestUtil.createFeatureConfig("id3", featureKey,
                                FeatureConfig.Status.StaticStatus.PUBLISHED, 2),
                        FeatureConfigTestUtil.createFeatureConfig("id4", featureKey,
                                FeatureConfig.Status.StaticStatus.PUBLISHED, 0)));

        customerFeatureConfigs.getByFeatureKey(new Feature.FeatureId(featureKey))
                .as(StepVerifier::create)
                .expectNextMatches(featureConfig -> featureConfig.getId().configId().equals("id4"))
                .expectNextMatches(featureConfig -> featureConfig.getId().configId().equals("id2"))
                .expectNextMatches(featureConfig -> featureConfig.getId().configId().equals("id3"))
                .verifyComplete();
    }
}