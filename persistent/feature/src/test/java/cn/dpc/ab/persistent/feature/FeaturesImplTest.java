package cn.dpc.ab.persistent.feature;

import cn.dpc.ab.domain.exception.RecordNotFoundException;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.Features;
import cn.dpc.ab.domain.feature.Features.FilterParams;
import cn.dpc.ab.persistent.feature.db.FeatureConfigDbRepository;
import cn.dpc.ab.persistent.feature.db.FeatureDb;
import cn.dpc.ab.persistent.feature.db.FeatureDbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static cn.dpc.ab.persistent.feature.FeatureTestUtil.createFeature;
import static cn.dpc.ab.persistent.feature.FeatureTestUtil.createFeatureDb;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeaturesImplTest {
    @Mock
    FeatureDbRepository repository;
    @Mock
    FeatureConfigDbRepository featureConfigDbRepository;

    Features features;

    @BeforeEach
    void setUp() {
        features = new FeaturesImpl(repository, featureConfigDbRepository);
    }

    @Test
    void should_add_success() {
        when(repository.save(any(FeatureDb.class))).thenAnswer(invocation -> {
            FeatureDb featureDb = invocation.getArgument(0);
            return Mono.just(featureDb);
        });

        features.add(createFeature("featureKey1"))
                .as(StepVerifier::create)
                .expectNextMatches(config -> config.getId().featureKey().equals("featureKey1")
                        && config.getDescription().updatedAt() != null
                        && config.getDescription().createdAt() != null
                )
                .verifyComplete();
    }

    @Test
    void should_add_return_error_when_repository_save_error() {
        Feature feature = createFeature("featureKey1");

        when(repository.save(any(FeatureDb.class))).thenReturn(Mono.error(new DuplicateKeyException("save error")));

        features.add(feature)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void should_update_success() {
        String featureKey = "featureKey1";
        Feature feature = createFeature(featureKey);
        FeatureDb db = FeatureDb.from(feature);
        Feature updatedFeature = createFeature(featureKey).update(Feature.Description.builder()
                        .description("description1")
                        .name("name1")
                        .enabled(true)
                .build());

        when(repository.findById(anyString())).thenReturn(Mono.just(db));
        when(repository.save(any(FeatureDb.class))).thenAnswer(invocation -> {
            FeatureDb featureDb = invocation.getArgument(0);
            return Mono.just(featureDb);
        });

        features.update(updatedFeature.getId(), updatedFeature.getDescription())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void should_update_throw_error_when_feature_not_found() {
        String featureKey = "not_exist";
        when(repository.findById(anyString())).thenReturn(Mono.empty());

        Feature updateFeature = createFeature(featureKey);

        features.update(updateFeature.getId(), updateFeature.getDescription())
                .as(StepVerifier::create)
                .expectError(RecordNotFoundException.class)
                .verify();
    }

    @Test
    void should_enable_success() {
        String featureKey = "featureKey1";
        Feature feature = createFeature(featureKey);
        FeatureDb db = FeatureDb.from(feature);

        when(repository.findById(anyString())).thenReturn(Mono.just(db));
        when(repository.save(any(FeatureDb.class))).thenAnswer(invocation -> {
            FeatureDb featureDb = invocation.getArgument(0);
            return Mono.just(featureDb);
        });

        features.enable(new Feature.FeatureId(featureKey))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void should_enable_return_error_when_feature_not_exist() {
        String featureKey = "not_exist";
        when(repository.findById(anyString())).thenReturn(Mono.empty());

        features.enable(new Feature.FeatureId(featureKey))
                .as(StepVerifier::create)
                .expectError(RecordNotFoundException.class)
                .verify();
    }

    @Test
    void should_disable_success() {
        String featureKey = "featureKey1";
        Feature feature = createFeature(featureKey);
        FeatureDb db = FeatureDb.from(feature);

        when(repository.findById(anyString())).thenReturn(Mono.just(db));
        when(repository.save(any(FeatureDb.class))).thenAnswer(invocation -> {
            FeatureDb featureDb = invocation.getArgument(0);
            return Mono.just(featureDb);
        });

        features.disable(new Feature.FeatureId(featureKey))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void should_disable_return_error_when_feature_not_exist() {
        String featureKey = "not_exist";
        when(repository.findById(anyString())).thenReturn(Mono.empty());

        features.disable(new Feature.FeatureId(featureKey))
                .as(StepVerifier::create)
                .expectError(RecordNotFoundException.class)
                .verify();
    }

    @Test
    void should_get_all_success() {
        when(repository.findAll()).thenReturn(Flux.just(FeatureTestUtil.createFeatureDb("featureKey1"),
                FeatureTestUtil.createFeatureDb("featureKey2"),
                FeatureTestUtil.createFeatureDb("featureKey3")));

        features.getAll(FilterParams.builder().key("3").build())
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void should_get_all_return_empty_when_no_record() {
        when(repository.findAll()).thenReturn(Flux.empty());

        features.getAll(FilterParams.builder().key("3").build())
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void should_get_success() {
        String featureKey = "featureKey1";
        when(repository.findById(anyString())).thenReturn(Mono.just(FeatureTestUtil.createFeatureDb(featureKey)));

        features.get(new Feature.FeatureId(featureKey))
                .as(StepVerifier::create)
                .expectNextMatches(config -> config.getId().featureKey().equals(featureKey))
                .verifyComplete();
    }

    @Test
    void should_get_return_empty_when_id_not_exist() {
        String featureKey = "not_exist";
        when(repository.findById(anyString())).thenReturn(Mono.empty());

        features.get(new Feature.FeatureId(featureKey))
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void should_subCollection_return_empty_when_no_record() {
        when(repository.findAll()).thenReturn(Flux.empty());

        features.subCollection(FilterParams.builder().build(), 0, 10)
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void should_subCollection_return_success() {
        when(repository.findAll()).thenReturn(Flux.just(FeatureTestUtil.createFeatureDb("featureKey1"),
                FeatureTestUtil.createFeatureDb("featureKey2"),
                FeatureTestUtil.createFeatureDb("featureKey3")));

        features.subCollection(FilterParams.builder().build(), 0, 2)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void should_subCollection_return_success_when_filter_by_key() {
        when(repository.findAll()).thenReturn(Flux.just(FeatureTestUtil.createFeatureDb("featureKey1"),
                FeatureTestUtil.createFeatureDb("featureKey2"),
                FeatureTestUtil.createFeatureDb("featureKey3")));

        features.subCollection(FilterParams.builder().key("2").build(), 0, 2)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void should_size_return_0_when_no_record() {
        when(repository.findAll()).thenReturn(Flux.empty());

        features.size(FilterParams.builder().build())
                .as(StepVerifier::create)
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void should_size_return_size_number() {
        when(repository.findAll()).thenReturn(Flux.just(FeatureTestUtil.createFeatureDb("featureKey1"),
                FeatureTestUtil.createFeatureDb("featureKey2"),
                FeatureTestUtil.createFeatureDb("featureKey3")));

        features.size(FilterParams.builder().build())
                .as(StepVerifier::create)
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void should_size_return_size_number_with_filter_params() {
        when(repository.findAll()).thenReturn(Flux.just(FeatureTestUtil.createFeatureDb("featureKey1"),
                FeatureTestUtil.createFeatureDb("featureKey2"),
                FeatureTestUtil.createFeatureDb("featureKey3")));

        features.size(FilterParams.builder().key("3").build())
                .as(StepVerifier::create)
                .expectNext(1L)
                .verifyComplete();
    }


    @Test
    void should_enabled_success() {
        when(repository.findAll()).thenReturn(Flux.just(createFeatureDb("featureKey1", true),
                FeatureTestUtil.createFeatureDb("featureKey2"),
                createFeatureDb("featureKey3", true)));

        features.enabled(FilterParams.builder().build())
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void should_enabled_return_empty_when_no_record() {
        when(repository.findAll()).thenReturn(Flux.empty());

        features.enabled(FilterParams.builder().build())
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void should_enabled_return_with_filter() {
        when(repository.findAll()).thenReturn(Flux.just(createFeatureDb("featureKey1", true),
                FeatureTestUtil.createFeatureDb("featureKey2"),
                createFeatureDb("featureKey3", true)));

        features.enabled(FilterParams.builder().key("3").build())
                .as(StepVerifier::create)
                .expectNextMatches(config -> config.getId().featureKey().equals("featureKey3"))
                .verifyComplete();
    }

}