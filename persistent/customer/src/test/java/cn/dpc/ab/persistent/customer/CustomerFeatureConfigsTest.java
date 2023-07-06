package cn.dpc.ab.persistent.customer;

import cn.dpc.ab.domain.core.CustomerId;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.persistent.feature.db.FeatureConfigDbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class CustomerFeatureConfigsTest {

    @Mock
    FeatureConfigDbRepository featureConfigDbRepository;

    private CustomerFeatureConfigs customerFeatureConfigs;

    @BeforeEach
    void setUp() {
        customerFeatureConfigs = new CustomerFeatureConfigs(featureConfigDbRepository,
                new CustomerId("customer-id", "clientId", Map.of()));
    }

    @Test
    void should_getByFeatureKey_return_empty_when_no_db_record() {
        String featureKey = "featureKey";
        Mockito.when(featureConfigDbRepository.findByFeatureKey(featureKey))
                .thenReturn(Flux.empty());

        customerFeatureConfigs.getByFeatureKey(new Feature.FeatureId(featureKey))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void should_getByFeatureKey_success() {
        String featureKey = "featureKey";
        Mockito.when(featureConfigDbRepository.findByFeatureKey(featureKey))
                .thenReturn(Flux.just(FeatureConfigTestUtil.createFeatureConfigDb("id1", featureKey,
                                FeatureConfig.Status.StaticStatus.DRAFT, 0),
                        FeatureConfigTestUtil.createFeatureConfigDb("id2", featureKey,
                                FeatureConfig.Status.StaticStatus.PUBLISHED, 1),
                        FeatureConfigTestUtil.createFeatureConfigDb("id3", featureKey,
                                FeatureConfig.Status.StaticStatus.PUBLISHED, 2),
                        FeatureConfigTestUtil.createFeatureConfigDb("id4", featureKey,
                                FeatureConfig.Status.StaticStatus.PUBLISHED, 0)));

        customerFeatureConfigs.getByFeatureKey(new Feature.FeatureId(featureKey))
                .as(StepVerifier::create)
                .expectNextMatches(featureConfig -> featureConfig.getId().configId().equals("id4"))
                .expectNextMatches(featureConfig -> featureConfig.getId().configId().equals("id2"))
                .expectNextMatches(featureConfig -> featureConfig.getId().configId().equals("id3"))
                .verifyComplete();
    }
}