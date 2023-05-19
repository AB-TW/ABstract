package cn.dpc.ab.persistent.feature;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.persistent.feature.db.FeatureDb;

import java.time.LocalDateTime;

/**
 * Feature test util.
 */
public class FeatureTestUtil {

    /**
     * Create a feature db.
     *
     * @param featureKey the feature key
     * @return the feature db
     */
    public static FeatureDb createFeatureDb(String featureKey) {
        return createFeatureDb(featureKey, false);
    }

    /**
     * Create a feature db.
     *
     * @param featureKey the feature key
     * @param enabled    the enabled
     * @return the feature db
     */
    public static FeatureDb createFeatureDb(String featureKey, boolean enabled) {
        FeatureDb featureDb = new FeatureDb();
        featureDb.setFeatureKey(featureKey);
        featureDb.setId(FeatureDb.idFromFeatureKey(featureKey));
        featureDb.setDescription(Feature.Description.builder()
                .description("description")
                .name("name")
                .enabled(enabled)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .template(null)
                .build());
        return featureDb;
    }

    /**
     * Create a feature.
     *
     * @param featureKey the feature key
     * @return the feature
     */
    public static Feature createFeature(String featureKey) {
        return new Feature(new Feature.FeatureId(featureKey),
                Feature.Description.builder()
                        .description("description")
                        .name("name")
                        .enabled(false)
                        .template(null)
                        .build());
    }
}
