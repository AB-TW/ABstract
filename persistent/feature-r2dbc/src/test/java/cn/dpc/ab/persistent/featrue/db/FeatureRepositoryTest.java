package cn.dpc.ab.persistent.featrue.db;

import cn.dpc.ab.domain.feature.Feature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@DataR2dbcTest
class FeatureRepositoryTest {

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private FeatureRepository featureRepository;

    @Test
    public void should_find_all_success() {
        FeatureDb featureDb = new FeatureDb();
        featureDb.setFeatureKey("test");
        featureDb.setName("test");
        featureDb.setDescription("test");
        featureDb.setTemplate(Feature.FeatureTemplate.builder()
                .dataType(Feature.FeatureTemplate.DataType.BOOLEAN)
                .defaultValue("true")
                .build());
        featureDb.setUpdatedAt(LocalDateTime.now());
        featureDb.setCreatedAt(LocalDateTime.now());
        featureDb.setEnabled(true);
        featureRepository.save(featureDb).block();
        featureRepository.findAll()
                .map(featureDb1 -> {
                    System.out.println(featureDb1);
                    return featureDb1;
                })
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }
}