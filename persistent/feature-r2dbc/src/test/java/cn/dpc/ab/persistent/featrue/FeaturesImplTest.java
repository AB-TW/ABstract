package cn.dpc.ab.persistent.featrue;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.Features;
import cn.dpc.ab.persistent.featrue.db.*;
import cn.dpc.ab.persistent.utils.JsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeaturesImplTest {
    @Mock
    private FeatureRepository repository;

    @Mock
    private CustomerFeatureRepository customerFeatureRepository;

    @Mock
    private FeatureConfigRepository featureConfigRepository;

    @Mock
    private CustomerFeatureConfigRepository customerFeatureConfigRepository;

    private Features features;

    @BeforeEach
    void setUp() {
        features = new FeaturesImpl(repository, customerFeatureRepository, featureConfigRepository, customerFeatureConfigRepository);
    }

    @Test
    void should_enable_success() {
        String featureKey = "testFeatureKey";
        FeatureDb db = FeatureTestUtil.createFeatureDb(featureKey);
        when(repository.findByFeatureKey(featureKey)).thenReturn(Mono.just(db));
        when(repository.save(any(FeatureDb.class))).thenReturn(Mono.just(db));

        features.enable(Feature.FeatureId.builder().featureKey(featureKey).build())
                .as(StepVerifier::create)
                .verifyComplete();
        Assertions.assertTrue(db.isEnabled());
    }

    @Test
    public void should_disable_success() {
        String featureKey = "testFeatureKey";
        FeatureDb db = FeatureTestUtil.createFeatureDb(featureKey);
        when(repository.findByFeatureKey(featureKey)).thenReturn(Mono.just(db));
        when(repository.save(any(FeatureDb.class))).thenReturn(Mono.just(db));

        features.disable(Feature.FeatureId.builder().featureKey(featureKey).build())
                .as(StepVerifier::create)
                .verifyComplete();
        Assertions.assertFalse(db.isEnabled());
    }

    @Test
    void should_add_success() {
        String featureKey = "testFeatureKey";
        FeatureDb db = FeatureTestUtil.createFeatureDb(featureKey);
        when(repository.save(any(FeatureDb.class))).thenReturn(Mono.just(db));

        features.add(db.to())
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void should_subCollection_success() {
        String featureKey = "testFeatureKey";
        int page = 1;
        int pageSize = 3;
        when(customerFeatureRepository.findAll(any(), eq(PageRequest.of(page, pageSize, Sort.by("updatedAt").descending()))))
                .thenReturn(Flux.just(FeatureTestUtil.createFeatureDb(featureKey),
                        FeatureTestUtil.createFeatureDb(featureKey),
                        FeatureTestUtil.createFeatureDb(featureKey)));

        features.subCollection(Features.FilterParams.builder().build(), 3, 6)
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void should_size_success() {
        when(customerFeatureRepository.count(any())).thenReturn(Mono.just(3L));

        features.size(Features.FilterParams.builder().build())
                .as(StepVerifier::create)
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void should_update_success() {
        String featureKey = "testFeatureKey";
        FeatureDb db = FeatureTestUtil.createFeatureDb(featureKey);
        when(repository.findByFeatureKey(featureKey)).thenReturn(Mono.just(db));
        when(repository.save(any(FeatureDb.class))).thenReturn(Mono.just(db));

        features.update(Feature.FeatureId.builder().featureKey(featureKey).build(), Feature.Description.builder()
                        .name("updatedName")
                        .description("updatedDescription")
                        .enabled(true)
                        .template(Feature.FeatureTemplate.builder().key("updatedKey").build())
                        .build())
                .as(StepVerifier::create)
                .verifyComplete();

        Assertions.assertEquals("updatedName", db.getName());
        Assertions.assertEquals("updatedDescription", db.getDescription());
        Assertions.assertTrue(db.isEnabled());
        Assertions.assertEquals(JsonUtil.toJson(Feature.FeatureTemplate.builder().key("updatedKey").build()), db.getTemplate());
    }

}