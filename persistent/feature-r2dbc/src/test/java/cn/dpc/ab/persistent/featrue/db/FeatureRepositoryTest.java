package cn.dpc.ab.persistent.featrue.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.UUID;

import static cn.dpc.ab.persistent.featrue.db.FeatureTestUtil.createFeatureDb;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest(properties = "logging.level.org.springframework.data.r2dbc=DEBUG")
class FeatureRepositoryTest {

    @Autowired
    private FeatureRepository featureRepository;

    @BeforeEach
    public void setUp() {
        featureRepository.deleteAll().block();
    }

    @Test
    public void should_find_all_success() {
        FeatureDb featureDb = createFeatureDb(UUID.randomUUID().toString());
        featureRepository.save(featureDb).block();

        featureRepository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void should_find_by_featureKey_success() {
        String featureKey = UUID.randomUUID().toString();
        FeatureDb featureDb = createFeatureDb(featureKey);
        featureRepository.save(featureDb).block();

        featureRepository.findByFeatureKey(featureKey)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        featureRepository.findByFeatureKey("notExistFeatureKey")
                .as(StepVerifier::create)
                .verifyComplete();
    }


}