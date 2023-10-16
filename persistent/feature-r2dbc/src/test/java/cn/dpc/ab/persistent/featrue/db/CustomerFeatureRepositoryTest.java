package cn.dpc.ab.persistent.featrue.db;

import cn.dpc.ab.domain.feature.Features;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static cn.dpc.ab.persistent.featrue.db.FeatureTestUtil.createFeatureDb;
import static org.springframework.data.domain.Sort.by;


@ExtendWith(SpringExtension.class)
@DataR2dbcTest(properties = "logging.level.org.springframework.data.r2dbc=DEBUG")
class CustomerFeatureRepositoryTest {
    @Autowired
    R2dbcEntityTemplate template;

    private CustomerFeatureRepository repository;

    @BeforeEach
    void setUp() {
        repository = new CustomerFeatureRepository(template);
        template.delete(FeatureDb.class).all().block();
    }

    @Test
    void findAll() {
        String featureKey1 = "key1";
        FeatureDb featureDb = createFeatureDb(featureKey1);
        template.insert(featureDb).block();

        String featureKey2 = "key2";
        FeatureDb featureDb2 = createFeatureDb(featureKey2);
        template.insert(featureDb2).block();

        Features.FilterParams params = Features.FilterParams.builder()
                .key("key")
                .build();
        repository.findAll(params)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();

        Features.FilterParams notExistParams = Features.FilterParams.builder()
                .key("notExistKey")
                .build();
        repository.findAll(notExistParams)
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_find_by_filterParams_success() {
        String featureKey1 = "key1";
        FeatureDb featureDb = createFeatureDb(featureKey1);
        template.insert(featureDb).block();

        String featureKey2 = "key2";
        FeatureDb featureDb2 = createFeatureDb(featureKey2);
        template.insert(featureDb2).block();

        Features.FilterParams params = Features.FilterParams.builder()
                .key("key")
                .build();
        repository.findAll(params)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();

        Features.FilterParams notExistParams = Features.FilterParams.builder()
                .key("notExistKey")
                .build();
        repository.findAll(notExistParams)
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_find_by_filterParams_with_page_success() {
        String featureKey1 = "key1";
        FeatureDb featureDb = createFeatureDb(featureKey1);
        template.insert(featureDb).block();

        String featureKey2 = "key2";
        FeatureDb featureDb2 = createFeatureDb(featureKey2);
        template.insert(featureDb2).block();


        Features.FilterParams params = Features.FilterParams.builder()
                .key("key")
                .build();

        PageRequest pageRequest = PageRequest.of(0, 1, by("featureKey").descending());
        repository.findAll(params, pageRequest)
                .as(StepVerifier::create)
                .expectNextMatches(feature -> feature.getFeatureKey().equals(featureKey2))
                .verifyComplete();

        Features.FilterParams notExistParams = Features.FilterParams.builder()
                .key("notExistKey")
                .build();
        repository.findAll(notExistParams, pageRequest)
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    public void should_count_by_filterParams_with_page_success() {
        String featureKey1 = "key1";
        FeatureDb featureDb = createFeatureDb(featureKey1);
        template.insert(featureDb).block();

        String featureKey2 = "key2";
        FeatureDb featureDb2 = createFeatureDb(featureKey2);
        template.insert(featureDb2).block();


        Features.FilterParams params = Features.FilterParams.builder()
                .key("key")
                .build();
        repository.count(params)
                .as(StepVerifier::create)
                .expectNext(2L)
                .verifyComplete();

        Features.FilterParams notExistParams = Features.FilterParams.builder()
                .key("notExistKey")
                .build();
        repository.count(notExistParams)
                .as(StepVerifier::create)
                .expectNext(0L)
                .verifyComplete();
    }
}