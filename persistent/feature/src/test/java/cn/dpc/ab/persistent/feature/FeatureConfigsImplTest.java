package cn.dpc.ab.persistent.feature;

import cn.dpc.ab.domain.exception.RecordNotFoundException;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.persistent.feature.db.FeatureConfigDbRepository;
import cn.dpc.ab.persistent.feature.db.FeatureConfigDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeatureConfigsImplTest {
    @Mock
    FeatureConfigDbRepository repository;

    Feature.FeatureConfigs featureConfigs;

    @BeforeEach
    public void setUp() {
        featureConfigs = new FeatureConfigsImpl(repository, Feature.FeatureId.builder().featureKey("featureKey").build());
    }

    @Test
    public void should_add_success() {
        when(repository.save(any(FeatureConfigDb.class)))
                .thenAnswer(invocation -> {
                    FeatureConfigDb featureConfigDb = invocation.getArgument(0);
                    return Mono.just(featureConfigDb);
                });

        featureConfigs.add(FeatureConfigTestUtil.createFeatureConfig(null, "featureKey"))
                .as(StepVerifier::create)
                .expectNextMatches(featureConfig -> featureConfig.getFeatureKey().featureKey().equals("featureKey")
                        && featureConfig.getDescription().updatedAt() != null
                        && featureConfig.getDescription().createdAt() != null)
                .verifyComplete();
    }

    @Test
    public void should_add_error_when_save_error() {
        when(repository.save(any(FeatureConfigDb.class)))
                .thenReturn(Mono.error(new RuntimeException("save error")));

        featureConfigs.add(FeatureConfigTestUtil.createFeatureConfig(null, "featureKey"))
                .as(StepVerifier::create)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void should_update_success() {
        when(repository.findById(any(String.class)))
                .thenAnswer(invocation ->
                        Mono.just(FeatureConfigTestUtil.createFeatureConfigDb(invocation.getArgument(0), "featureKey"))
                );

        when(repository.save(any(FeatureConfigDb.class)))
                .thenAnswer(invocation -> {
                    FeatureConfigDb featureConfigDb = invocation.getArgument(0);
                    return Mono.just(featureConfigDb);
                });
        String id = "id";
        featureConfigs.update(new FeatureConfig.FeatureConfigId(id),
                        FeatureConfigTestUtil.createFeatureConfig("featureConfigId", "featureKey").getDescription())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_update_return_not_found_when_record_not_exist() {
        when(repository.findById(any(String.class)))
                .thenReturn(Mono.empty());

        featureConfigs.update(new FeatureConfig.FeatureConfigId("id"),
                        FeatureConfigTestUtil.createFeatureConfig("featureConfigId", "featureKey").getDescription())
                .as(StepVerifier::create)
                .expectError(RecordNotFoundException.class)
                .verify();
    }

    @Test
    public void should_update_return_error_when_save_error() {
        when(repository.findById(any(String.class)))
                .thenAnswer(invocation ->
                        Mono.just(FeatureConfigTestUtil.createFeatureConfigDb(invocation.getArgument(0), "featureKey"))
                );

        when(repository.save(any(FeatureConfigDb.class)))
                .thenReturn(Mono.error(new RuntimeException("save error")));
        String id = "id";
        featureConfigs.update(new FeatureConfig.FeatureConfigId(id),
                        FeatureConfigTestUtil.createFeatureConfig("featureConfigId", "featureKey").getDescription())
                .as(StepVerifier::create)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void should_delete_success() {
        when(repository.findById(any(String.class)))
                .thenAnswer(invocation ->
                        Mono.just(FeatureConfigTestUtil.createFeatureConfigDb(invocation.getArgument(0), "featureKey"))
                );

        when(repository.delete(any(FeatureConfigDb.class)))
                .thenReturn(Mono.empty());

        featureConfigs.delete(new FeatureConfig.FeatureConfigId("id"))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_delete_logic_delete_when_status_is_not_draft() {
        when(repository.findById(any(String.class)))
                .thenAnswer(invocation ->
                        Mono.just(FeatureConfigTestUtil.createFeatureConfigDb(invocation.getArgument(0), "featureKey",
                                FeatureConfig.Status.StaticStatus.PUBLISHED))
                );

        when(repository.save(any(FeatureConfigDb.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        featureConfigs.delete(new FeatureConfig.FeatureConfigId("id"))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_delete_success_when_record_not_exist() {
        when(repository.findById(any(String.class)))
                .thenReturn(Mono.empty());

        featureConfigs.delete(new FeatureConfig.FeatureConfigId("id"))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_publish_success() {
        when(repository.findById(any(String.class)))
                .thenAnswer(invocation ->
                        Mono.just(FeatureConfigTestUtil.createFeatureConfigDb(invocation.getArgument(0), "featureKey"))
                );

        when(repository.save(any(FeatureConfigDb.class)))
                .thenAnswer(invocation -> {
                    FeatureConfigDb featureConfigDb = invocation.getArgument(0);
                    return Mono.just(featureConfigDb);
                });

        featureConfigs.publish(new FeatureConfig.FeatureConfigId("id"))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_publish_return_not_found_when_record_not_exist() {
        when(repository.findById(any(String.class)))
                .thenReturn(Mono.empty());

        featureConfigs.publish(new FeatureConfig.FeatureConfigId("id"))
                .as(StepVerifier::create)
                .expectError(RecordNotFoundException.class)
                .verify();
    }

    @Test
    public void should_publish_return_error_when_save_error() {
        when(repository.findById(any(String.class)))
                .thenAnswer(invocation ->
                        Mono.just(FeatureConfigTestUtil.createFeatureConfigDb(invocation.getArgument(0), "featureKey"))
                );

        when(repository.save(any(FeatureConfigDb.class)))
                .thenReturn(Mono.error(new RuntimeException("save error")));

        featureConfigs.publish(new FeatureConfig.FeatureConfigId("id"))
                .as(StepVerifier::create)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void should_disable_success() {
        when(repository.findById(any(String.class)))
                .thenAnswer(invocation ->
                        Mono.just(FeatureConfigTestUtil.createFeatureConfigDb(invocation.getArgument(0), "featureKey"))
                );

        when(repository.save(any(FeatureConfigDb.class)))
                .thenAnswer(invocation -> {
                    FeatureConfigDb featureConfigDb = invocation.getArgument(0);
                    return Mono.just(featureConfigDb);
                });

        featureConfigs.disable(new FeatureConfig.FeatureConfigId("id"))
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_disable_return_not_found_when_record_not_exist() {
        when(repository.findById(any(String.class)))
                .thenReturn(Mono.empty());

        featureConfigs.disable(new FeatureConfig.FeatureConfigId("id"))
                .as(StepVerifier::create)
                .expectError(RecordNotFoundException.class)
                .verify();
    }

    @Test
    public void should_disable_return_error_when_save_error() {
        when(repository.findById(any(String.class)))
                .thenAnswer(invocation ->
                        Mono.just(FeatureConfigTestUtil.createFeatureConfigDb(invocation.getArgument(0), "featureKey"))
                );

        when(repository.save(any(FeatureConfigDb.class)))
                .thenReturn(Mono.error(new RuntimeException("save error")));

        featureConfigs.disable(new FeatureConfig.FeatureConfigId("id"))
                .as(StepVerifier::create)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void should_getAll_success() {
        var featureKey = "featureKey";
        when(repository.findByFeatureKey(any(String.class)))
                .thenReturn(Flux.just(FeatureConfigTestUtil.createFeatureConfigDb("id1", featureKey),
                        FeatureConfigTestUtil.createFeatureConfigDb("id2", featureKey)));

        featureConfigs.getAll(Feature.FeatureConfigs.FilterParams.builder().build())
                .as(StepVerifier::create)
                .expectNextMatches(featureConfig -> featureConfig.getId().configId().equals("id2"))
                .expectNextMatches(featureConfig -> featureConfig.getId().configId().equals("id1"))
                .verifyComplete();
    }

    @Test
    public void should_getAll_return_empty_when_db_record_is_empty() {
        when(repository.findByFeatureKey(any(String.class)))
                .thenReturn(Flux.empty());

        featureConfigs.getAll(Feature.FeatureConfigs.FilterParams.builder().build())
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_subCollection_success() {
        when(repository.findByFeatureKey(any(String.class)))
                .thenReturn(Flux.just(FeatureConfigTestUtil.createFeatureConfigDb("id1", "featureKey"),
                        FeatureConfigTestUtil.createFeatureConfigDb("id2", "featureKey")));

        featureConfigs.subCollection(Feature.FeatureConfigs.FilterParams.builder().build(), 0, 1)
                .as(StepVerifier::create)
                .expectNextMatches(featureConfig -> featureConfig.getId().configId().equals("id2"))
                .verifyComplete();
    }

    @Test
    public void should_size_success() {
        when(repository.findByFeatureKey(any(String.class)))
                .thenReturn(Flux.just(FeatureConfigTestUtil.createFeatureConfigDb("id1", "featureKey"),
                        FeatureConfigTestUtil.createFeatureConfigDb("id2", "featureKey")));

        featureConfigs.size(Feature.FeatureConfigs.FilterParams.builder().build())
                .as(StepVerifier::create)
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    public void should_get_success() {
        when(repository.findById(any(String.class)))
                .thenReturn(Mono.just(FeatureConfigTestUtil.createFeatureConfigDb("id", "featureKey")));

        featureConfigs.get(new FeatureConfig.FeatureConfigId("id"))
                .as(StepVerifier::create)
                .expectNextMatches(featureConfig -> featureConfig.getId().configId().equals("id"))
                .verifyComplete();
    }

    @Test
    public void should_get_return_empty_when_no_record() {
        when(repository.findById(any(String.class)))
                .thenReturn(Mono.empty());

        featureConfigs.get(new FeatureConfig.FeatureConfigId("id"))
                .as(StepVerifier::create)
                .verifyComplete();
    }
}