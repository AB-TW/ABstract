package cn.dpc.ab.persistent.feature.db;

import cn.dpc.ab.persistent.RepositoryTestBase;
import cn.dpc.ab.persistent.feature.FeatureTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;


class FeatureDbRepositoryTest extends RepositoryTestBase {
    @Autowired
    FeatureDbRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll().block();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll().block();
    }

    @Test
    void should_save_Feature_success() {
        var featureKey = "featureKey1";
        repository.save(FeatureTestUtil.createFeatureDb(featureKey))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void should_add_same_featureKey_fail() {
        var featureKey = "featureKey1";
        repository.save(FeatureTestUtil.createFeatureDb(featureKey)).block();

        repository.save(FeatureTestUtil.createFeatureDb(featureKey))
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void should_update_same_featureKey_fail() {
        var featureKey = "featureKey1";
        FeatureDb db = repository.save(FeatureTestUtil.createFeatureDb(featureKey)).block();

        assert db != null;
        FeatureDb db1 = FeatureDb.from(db.to().enable());
        db1.setVersion(db.getVersion());

        repository.save(db1)
                .as(StepVerifier::create)
                .expectNextMatches(config -> config.getDescription().enabled())
                .verifyComplete();
    }

    @Test
    void should_getById_success() {
        var featureKey = "featureKey1";
        repository.save(FeatureTestUtil.createFeatureDb(featureKey)).block();

        repository.findById(FeatureDb.idFromFeatureKey(featureKey))
                .as(StepVerifier::create)
                .expectNextMatches(config -> config.getFeatureKey().equals(featureKey))
                .verifyComplete();
    }

    @Test
    void should_findAll_success() {
        repository.save(FeatureTestUtil.createFeatureDb("key1")).block();
        repository.save(FeatureTestUtil.createFeatureDb("key2")).block();
        repository.save(FeatureTestUtil.createFeatureDb("key3")).block();

        repository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();
    }

}