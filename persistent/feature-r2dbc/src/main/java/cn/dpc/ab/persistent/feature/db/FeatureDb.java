package cn.dpc.ab.persistent.feature.db;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.persistent.utils.JsonUtil;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * FeatureDb represents a feature in database.
 */
@Data
@Table("feature")
public class FeatureDb {

    @Id
    private Long id;

    private String featureKey;

    private String name;

    private String description;

    private String template;

    private boolean enabled;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public void setTemplate(Feature.FeatureTemplate template) {
        this.template = JsonUtil.toJson(template);
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * Convert a feature to FeatureDb.
     *
     * @param feature the feature to convert
     * @return the FeatureDb
     */
    public static FeatureDb from(Feature feature) {
        FeatureDb featureDb = new FeatureDb();
        featureDb.setFeatureKey(feature.getId().featureKey());
        featureDb.setName(feature.getDescription().name());
        featureDb.setDescription(feature.getDescription().description());
        featureDb.setTemplate(feature.getDescription().template());
        featureDb.setEnabled(feature.getDescription().enabled());
        featureDb.setCreatedAt(Optional.ofNullable(feature.getDescription().createdAt()).orElse(LocalDateTime.now()));
        featureDb.setUpdatedAt(Optional.ofNullable(feature.getDescription().updatedAt()).orElse(LocalDateTime.now()));
        return featureDb;
    }

    /**
     * Convert a FeatureDb to feature.
     *
     * @return the feature
     */
    public Feature to() {
        return Feature.builder()
                .id(new Feature.FeatureId(featureKey))
                .description(new Feature.Description(name, description,
                        JsonUtil.fromJson(template, Feature.FeatureTemplate.class),
                        enabled, createdAt, updatedAt))
                .build();
    }

    /**
     * Update a feature description.
     *
     * @param description the description to update
     */
    public void updateDescription(Feature.Description description) {
        this.setName(description.name());
        this.setDescription(description.description());
        this.setTemplate(description.template());
        this.setEnabled(description.enabled());
        this.setUpdatedAt(Optional.ofNullable(description.updatedAt()).orElse(LocalDateTime.now()));
    }
}
