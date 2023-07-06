package cn.dpc.ab.persistent.customer;

import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.persistent.feature.db.FeatureConfigDb;

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
     * @param id the id of the FeatureConfig
     * @param featureKey the featureKey of the FeatureConfig
     * @param staticStatus the staticStatus of the FeatureConfig
     * @return the FeatureConfig
     */
    public static FeatureConfigDb createFeatureConfigDb(String id, String featureKey,
                                                        FeatureConfig.Status.StaticStatus staticStatus, long priority) {
        FeatureConfigDb featureConfigDb = new FeatureConfigDb();

        featureConfigDb.setId(id);
        featureConfigDb.setFeatureKey(featureKey);
        FeatureConfig.Description description = FeatureConfig.Description.builder()
                .title("title")
                .description("description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .key("key")
                .data(List.of(Map.of("key1", "key2")))
                .saData(Map.of("key1", "key2"))
                .status(FeatureConfig.Status.builder()
                        .staticStatus(staticStatus)
                        .build())
                .priority(priority)
                .build();
        featureConfigDb.setDescription(FeatureConfigDb.Description.from(description));

        return featureConfigDb;
    }
}
