package cn.dpc.ab.persistent.feature.db;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.Feature.FeatureDescription;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.IdAttribute;
import org.springframework.data.couchbase.core.mapping.id.IdPrefix;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.USE_ATTRIBUTES;

/**
 * FeatureFlagDb represents a feature flag in the database.
 */
@Document
@Data
public class FeatureDb {

    @Version
    private long version;

    @IdPrefix
    private static final String PREFIX = "feature_flag";

    @Id
    @GeneratedValue(strategy = USE_ATTRIBUTES)
    private String id;

    @IdAttribute
    private String featureKey;

    @Field
    private FeatureDescription description;


    public static String idFromFeatureKey(String featureKey) {
        return PREFIX + "." + featureKey;
    }

    /**
     * Creates a FeatureFlagDb from a FeatureFlag.
     *
     * @param feature the FeatureFlag
     * @return the FeatureFlagDb
     */
    public static FeatureDb from(Feature feature) {
        FeatureDb db = new FeatureDb();
        db.setId(idFromFeatureKey(feature.getId().featureKey()));
        db.setFeatureKey(feature.getId().featureKey());
        db.setDescription(from(feature.getDescription()));
        return db;
    }

    private static FeatureDescription from(FeatureDescription description) {
        return FeatureDescription.builder()
                .name(description.name())
                .description(description.description())
                .enabled(description.enabled())
                .createdAt(Optional.ofNullable(description.createdAt()).orElse(LocalDateTime.now()))
                .updatedAt(Optional.ofNullable(description.updatedAt()).orElse(LocalDateTime.now()))
                .template(description.template())
                .build();
    }

    public Feature to() {
        return new Feature(Feature.FeatureId.builder().featureKey(this.getFeatureKey()).build(),
                this.getDescription());
    }
}
