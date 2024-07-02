package cn.dpc.ab.persistent.customer;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * FeatureConfigTestUtil provides utility methods for FeatureConfig tests.
 */
public class FeatureConfigTestUtil {


    /**
     * Creates a FeatureConfig from a Feature.
     *
     * @param featureKey the featureKey of the FeatureConfig
     * @param staticStatus the staticStatus of the FeatureConfig
     * @return the FeatureConfig
     */
    public static FeatureConfig createFeatureConfig(String id, String featureKey,
                                                        FeatureConfig.Status.StaticStatus staticStatus, long priority) {
        return FeatureConfig.builder()
                .id(FeatureConfig.FeatureConfigId.builder().configId(id).build())
                .featureKey(Feature.FeatureId.builder().featureKey(featureKey).build())
                .description(FeatureConfig.Description.builder()
                        .title("title")
                        .description("description")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .key("featureKey")
                        .data(List.of(Map.of("key1", "key2")))
                        .saData(Map.of("key1", "key2"))
                        .status(FeatureConfig.Status.builder()
                                .staticStatus(staticStatus)
                                .build())
                        .priority(priority)
                        .build())
                .build();
    }
}
