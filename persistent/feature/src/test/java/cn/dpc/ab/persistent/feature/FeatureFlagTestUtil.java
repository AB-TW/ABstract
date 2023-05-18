package cn.dpc.ab.persistent.feature;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.persistent.feature.db.FeatureDb;

import java.time.LocalDateTime;

/**
 * Feature flag test util.
 */
public class FeatureFlagTestUtil {

    /**
     * Create a feature flag db.
     *
     * @param featureKey the feature key
     * @return the feature flag db
     */
    public static FeatureDb createFeatureFlagDb(String featureKey) {
        return createFeatureFlagDb(featureKey, false);
    }

    /**
     * Create a feature flag db.
     *
     * @param featureKey the feature key
     * @param enabled    the enabled
     * @return the feature flag db
     */
    public static FeatureDb createFeatureFlagDb(String featureKey, boolean enabled) {
        FeatureDb featureDb = new FeatureDb();
        featureDb.setFeatureKey(featureKey);
        featureDb.setId(FeatureDb.idFromFeatureKey(featureKey));
        featureDb.setDescription(Feature.FeatureDescription.builder()
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
     * Create a feature flag.
     *
     * @param featureKey the feature key
     * @return the feature flag
     */
    public static Feature createFeatureFlag(String featureKey) {
        return new Feature(new Feature.FeatureId(featureKey),
                Feature.FeatureDescription.builder()
                        .description("description")
                        .name("name")
                        .enabled(false)
                        .template(null)
                        .build());
    }
}
