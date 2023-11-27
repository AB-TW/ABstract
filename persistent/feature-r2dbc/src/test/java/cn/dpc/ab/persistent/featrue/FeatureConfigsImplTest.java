package cn.dpc.ab.persistent.featrue;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.persistent.featrue.db.CustomerFeatureConfigRepository;
import cn.dpc.ab.persistent.featrue.db.FeatureConfigDb;
import cn.dpc.ab.persistent.featrue.db.FeatureConfigRepository;
import cn.dpc.ab.persistent.featrue.db.FeatureTestUtil;
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

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeatureConfigsImplTest {
    @Mock
    private FeatureConfigRepository repository;

    @Mock
    private CustomerFeatureConfigRepository customerRepository;

    private final Feature.FeatureId featureId = Feature.FeatureId.builder().featureKey("featureKey").build();

    private Feature.Configs configs;

    @BeforeEach
    public void setUp() {
        configs = new FeatureConfigsImpl(repository, customerRepository, featureId);
    }

    @Test
    void should_get_success() {
        FeatureConfigDb db = FeatureTestUtil.generateFeatureConfig(featureId.featureKey());
        when(repository.findById(String.valueOf(db.getId()))).thenReturn(Mono.just(db));

        configs.get(FeatureConfig.FeatureConfigId.builder().configId(String.valueOf(db.getId())).build())
                .as(StepVerifier::create)
                .expectNextMatches(item -> Objects.equals(item.getDescription().data(), db.to().getDescription().data()))
                .verifyComplete();
    }

    @Test
    void should_add_success() {
        FeatureConfigDb db = FeatureTestUtil.generateFeatureConfig(featureId.featureKey());
        when(repository.save(any(FeatureConfigDb.class))).thenReturn(Mono.just(db));

        configs.add(db.to())
                .as(StepVerifier::create)
                .expectNextMatches(item -> Objects.equals(item.getDescription().data(), db.to().getDescription().data()))
                .verifyComplete();
    }

    @Test
    void should_update_success() {
        FeatureConfigDb db = FeatureTestUtil.generateFeatureConfig(featureId.featureKey());
        db.setId(1L);
        when(repository.findById(String.valueOf(db.getId()))).thenReturn(Mono.just(db));
        when(repository.save(any(FeatureConfigDb.class))).thenReturn(Mono.just(db));

        configs.update(FeatureConfig.FeatureConfigId.builder().configId(String.valueOf(db.getId())).build(),
                        FeatureConfig.Description.builder()
                                .title("updateTitle")
                                .description("updateDescription")
                                .key("updateKey")
                                .data(db.to().getDescription().data())
                                .status(db.to().getDescription().status())
                                .build())
                .as(StepVerifier::create)
                .verifyComplete();

        assertEquals(db.getTitle(), "updateTitle");
        assertEquals(db.getDescription(), "updateDescription");
        assertEquals(db.getKey(), "updateKey");
    }

    @Test
    void should_delete_success() {
        FeatureConfigDb db = FeatureTestUtil.generateFeatureConfig(featureId.featureKey());
        db.setId(1L);
        when(repository.findById(String.valueOf(db.getId()))).thenReturn(Mono.just(db));
        when(repository.save(any(FeatureConfigDb.class))).thenReturn(Mono.just(db));

        configs.delete(FeatureConfig.FeatureConfigId.builder().configId(String.valueOf(db.getId())).build())
                .as(StepVerifier::create)
                .verifyComplete();

        assertEquals(db.getStaticStatus(), FeatureConfig.Status.StaticStatus.DELETED);
    }

    @Test
    public void should_publish_success() {
        FeatureConfigDb db = FeatureTestUtil.generateFeatureConfig(featureId.featureKey());
        db.setId(1L);
        when(repository.findById(String.valueOf(db.getId()))).thenReturn(Mono.just(db));
        when(repository.save(any(FeatureConfigDb.class))).thenReturn(Mono.just(db));

        configs.publish(FeatureConfig.FeatureConfigId.builder().configId(String.valueOf(db.getId())).build())
                .as(StepVerifier::create)
                .verifyComplete();

        assertEquals(db.getStaticStatus(), FeatureConfig.Status.StaticStatus.PUBLISHED);
    }

    @Test
    public void should_disable_success() {
        FeatureConfigDb db = FeatureTestUtil.generateFeatureConfig(featureId.featureKey());
        db.setId(1L);
        when(repository.findById(String.valueOf(db.getId()))).thenReturn(Mono.just(db));
        when(repository.save(any(FeatureConfigDb.class))).thenReturn(Mono.just(db));

        configs.disable(FeatureConfig.FeatureConfigId.builder().configId(String.valueOf(db.getId())).build())
                .as(StepVerifier::create)
                .verifyComplete();

        assertEquals(db.getStaticStatus(), FeatureConfig.Status.StaticStatus.DISABLED);
    }

    @Test
    public void should_size_success() {
        when(customerRepository.count(featureId.featureKey(), Feature.Configs.FilterParams.builder().build())).thenReturn(Mono.just(3L));

        configs.size(Feature.Configs.FilterParams.builder().build())
                .as(StepVerifier::create)
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    public void should_subCollection_success() {
        int page = 1;
        int pageSize = 3;
        when(customerRepository.findAll(eq(featureId.featureKey()), any(),
                eq(PageRequest.of(page, pageSize, Sort.by("updatedAt").descending()))))
                .thenReturn(Flux.just(FeatureTestUtil.generateFeatureConfig(featureId.featureKey())));

        configs.subCollection(Feature.Configs.FilterParams.builder().build(), 3, 6)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void should_all_success() {
        when(customerRepository.findAll(eq(featureId.featureKey()), any()))
                .thenReturn(Flux.just(FeatureTestUtil.generateFeatureConfig(featureId.featureKey())));

        configs.all(Feature.Configs.FilterParams.builder().build())
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

}