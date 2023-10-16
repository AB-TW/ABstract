package cn.dpc.ab.persistent.featrue.db;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * FeatureTestUtil is the test util for FeatureDb.
 */
public class FeatureTestUtil {

    /**
     * Create a FeatureDb.
     *
     * @param featureKey the feature key
     * @return the FeatureDb
     */
    public static FeatureDb createFeatureDb(String featureKey) {
        FeatureDb featureDb = new FeatureDb();
        featureDb.setFeatureKey(featureKey);
        featureDb.setName("test");
        featureDb.setDescription("test");
        featureDb.setTemplate(Feature.FeatureTemplate.builder()
                .dataType(Feature.FeatureTemplate.DataType.BOOLEAN)
                .defaultValue("true")
                .build());
        featureDb.setUpdatedAt(LocalDateTime.now());
        featureDb.setCreatedAt(LocalDateTime.now());
        featureDb.setEnabled(true);
        return featureDb;
    }

    /**
     * Generate a FeatureConfigDb.
     *
     * @param featureKey the feature key
     * @return the FeatureConfigDb
     */
    public static FeatureConfigDb generateFeatureConfig(String featureKey) {
        FeatureConfig featureConfig = FeatureConfig.builder()
                .featureKey(Feature.FeatureId.builder().featureKey(featureKey).build())
                .description(FeatureConfig.Description.builder()
                        .key(UUID.randomUUID().toString())
                        .title("title")
                        .description("description")
                        .data(Map.of("key", "value"))
                        .saData(Map.of("key", "value"))
                        .status(FeatureConfig.Status.builder()
                                .staticStatus(FeatureConfig.Status.StaticStatus.PUBLISHED)
                                .build())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .filterCondition(new FilterCondition())
                        .build())
                .build();
        return FeatureConfigDb.from(featureConfig);
    }
}
