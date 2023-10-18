package cn.dpc.ab.persistent.featrue.db;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static cn.dpc.ab.persistent.featrue.db.FeatureTestUtil.generateFeatureConfig;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest(properties = "logging.level.org.springframework.data.r2dbc=DEBUG")
class CustomerFeatureConfigRepositoryTest {
    @Autowired
    R2dbcEntityTemplate template;

    private CustomerFeatureConfigRepository repository;

    @BeforeEach
    void setUp() {
        repository = new CustomerFeatureConfigRepository(template);
        template.delete(FeatureConfigDb.class).all().block();
    }

    @Test
    public void should_find_all_success() {
        String featureKey = "featureKey";
        template.insert(generateFeatureConfig(featureKey)).block();
        template.insert(generateFeatureConfig(featureKey)).block();

        repository.findAll(featureKey, Feature.Configs.FilterParams.builder()
                        .status(FeatureConfig.Status.DynamicStatus.RUNNING)
                        .title("tit")
                        .build())
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void should_find_all_with_page_success() {
        String featureKey = "featureKey";
        template.insert(generateFeatureConfig(featureKey)).block();
        template.insert(generateFeatureConfig(featureKey)).block();
        FeatureConfigDb item3 = generateFeatureConfig(featureKey);
        item3.setTitle("titleItems");
        template.insert(item3).block();

        PageRequest pageRequest = PageRequest.of(0, 1).withSort(Sort.by("updatedAt").descending());

        repository.findAll(featureKey, Feature.Configs.FilterParams.builder()
                        .status(FeatureConfig.Status.DynamicStatus.RUNNING)
                        .title("tit")
                        .build(), pageRequest)
                .as(StepVerifier::create)
                .expectNextMatches(item -> item.getTitle().equals(item3.getTitle()))
                .verifyComplete();
    }

    @Test
    public void should_count_success() {
        String featureKey = "featureKey";
        template.insert(generateFeatureConfig(featureKey)).block();
        template.insert(generateFeatureConfig(featureKey)).block();
        FeatureConfigDb item3 = generateFeatureConfig(featureKey);
        item3.setTitle("item3");
        template.insert(item3).block();

        repository.count(featureKey, Feature.Configs.FilterParams.builder()
                .status(FeatureConfig.Status.DynamicStatus.RUNNING)
                .title("tit")
                .build())
                .as(StepVerifier::create)
                .expectNext(2L)
                .verifyComplete();
    }
}
