package cn.dpc.ab.persistent.featrue.db;

import cn.dpc.ab.domain.feature.Feature;

import java.time.LocalDateTime;

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
}
