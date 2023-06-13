package cn.dpc.ab.persistent.feature.db;

import cn.dpc.ab.persistent.RepositoryTestBase;
import cn.dpc.ab.persistent.feature.FeatureConfigTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.List;

/**
 * FeatureConfigDbRepositoryTest provides a skeleton implementation of the FeatureConfigDbRepositoryTest interface.
 */
public class FeatureConfigDbRepositoryTest extends RepositoryTestBase {

    @Autowired
    FeatureConfigDbRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll().block();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll().block();
    }

    @Test
    public void should_save_feature_config_success() {
        String id = "id";
        String featureKey = "feature1";

        repository.save(FeatureConfigTestUtil.createFeatureConfigDb(id, featureKey))
                .as(StepVerifier::create)
                .expectNextMatches(config -> config.getId().equals(id) && config.getFeatureKey().equals(featureKey)
                        && config.getDescription().getData() instanceof List<?>)
                .verifyComplete();
    }

    @Test
    public void should_find_by_featureKey_success() {
        String id = "id";
        String featureKey = "feature1";

        repository.save(FeatureConfigTestUtil.createFeatureConfigDb(id, featureKey)).block();
        repository.save(FeatureConfigTestUtil.createFeatureConfigDb("id2", "featureKey2")).block();

        repository.findByFeatureKey(featureKey)
                .as(StepVerifier::create)
                .expectNextMatches(config -> config.getId().equals(id) && config.getFeatureKey().equals(featureKey)
                        && config.getDescription().getData() instanceof List<?>)
                .verifyComplete();
    }
}
