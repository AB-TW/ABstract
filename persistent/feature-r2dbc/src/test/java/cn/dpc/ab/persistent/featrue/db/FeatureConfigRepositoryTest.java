package cn.dpc.ab.persistent.featrue.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static cn.dpc.ab.persistent.featrue.db.FeatureTestUtil.generateFeatureConfig;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest(properties = "logging.level.org.springframework.data.r2dbc=DEBUG")
class FeatureConfigRepositoryTest {

    @Autowired
    private FeatureConfigRepository featureConfigRepository;

    @BeforeEach
    public void setUp() {
        featureConfigRepository.deleteAll().block();
    }

    @Test
    public void should_findAll_success() {
        featureConfigRepository.save(generateFeatureConfig("featureKey")).block();

        featureConfigRepository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

}
