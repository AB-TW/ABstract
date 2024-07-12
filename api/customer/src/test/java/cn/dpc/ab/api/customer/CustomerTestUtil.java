package cn.dpc.ab.api.customer;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * CustomerTestUtil represents the test util for customers.
 */
public class CustomerTestUtil {

    /**
     * createFeatureConfig creates a feature config.
     *
     * @param id         id
     * @param featureKey key
     * @return FeatureConfig
     */
    public static FeatureConfig createFeatureConfig(String id, String featureKey) {
        return FeatureConfig.builder()
                .id(FeatureConfig.FeatureConfigId.builder().configId(null == id ? UUID.randomUUID().toString() : id).build())
                .featureKey(Feature.FeatureId.builder().featureKey(featureKey).build())
                .description(FeatureConfig.Description.builder()
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .description("description")
                        .title("title")
                        .data(Map.of("key", "value"))
                        .saData(Map.of("key", "value"))
                        .priority(0)
                        .status(FeatureConfig.Status.builder()
                                .staticStatus(FeatureConfig.Status.StaticStatus.DRAFT)
                                .build())
                        .build())
                .build();
    }
}
