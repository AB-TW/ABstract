package cn.dpc.ab.domain.feature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * FeatureConfig test.
 */
class FeatureConfigTest {
    @Test
    void should_publish_feature_config() {
        // given
        FeatureConfig featureConfig = getFeatureConfig();
        // when
        featureConfig.publish();
        // then
        Assertions.assertEquals(featureConfig.getDescription().status().getStatus(),
                FeatureConfig.Status.DynamicStatus.RUNNING, "should be running");
    }


    @Test
    void should_disable_feature_config() {
        // given
        FeatureConfig featureConfig = getFeatureConfig();
        // when
        featureConfig.disable();
        // then
        Assertions.assertEquals(featureConfig.getDescription().status().getStatus(),
                FeatureConfig.Status.DynamicStatus.DISABLED, "should be disabled");
    }

    @Test
    void should_delete_feature_config() {
        // given
        FeatureConfig featureConfig = getFeatureConfig();
        // when
        featureConfig.delete();
        // then
        Assertions.assertEquals(featureConfig.getDescription().status().getStatus(),
                FeatureConfig.Status.DynamicStatus.DELETED, "should be deleted");
    }

    @Test
    void should_update_feature_config() {
        // given
        FeatureConfig featureConfig = getFeatureConfig();
        // when
        featureConfig.update(FeatureConfig.Description.builder()
                .title("title")
                .description("description")
                .data("data1")
                .saData(Map.of("key", "value"))
                .status(FeatureConfig.Status.builder()
                        .staticStatus(FeatureConfig.Status.StaticStatus.DRAFT)
                        .startTime(null)
                        .endTime(null)
                        .build())
                .priority(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        // then
        Assertions.assertEquals(featureConfig.getDescription().data(),
                "data1", "should be updated");
    }

    private static FeatureConfig getFeatureConfig() {
        return FeatureConfig.builder()
                .id(new FeatureConfig.FeatureConfigId("configId"))
                .description(FeatureConfig.Description.builder()
                        .title("title")
                        .description("description")
                        .data("data")
                        .saData(Map.of("key", "value"))
                        .status(FeatureConfig.Status.builder()
                                .staticStatus(FeatureConfig.Status.StaticStatus.DRAFT)
                                .startTime(null)
                                .endTime(null)
                                .build())
                        .priority(0)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
                )
                .featureKey(new Feature.FeatureId("featureKey"))
                .build();
    }
}