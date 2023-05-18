package cn.dpc.ab.domain.feature;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * FeatureFlag represents a feature flag.
 */
@AllArgsConstructor
@Getter
public class Feature {
    private FeatureId id;
    private FeatureDescription description;

    /**
     * Enable a feature flag.
     *
     * @return a Mono that completes when the feature flag has been enabled
     */
    public Feature enable() {
        this.description = new FeatureDescription(description.name(),
                description.description(),
                description.template(),
                true,
                description.createdAt(),
                LocalDateTime.now());

        return this;
    }

    /**
     * Update a feature flag.
     *
     * @param description the new description of the feature flag
     * @return a Mono that completes when the feature flag has been updated
     */
    public Feature update(FeatureDescription description) {
        this.description = new FeatureDescription(description.name(),
                description.description(),
                description.template(),
                description.enabled(),
                description.createdAt(),
                LocalDateTime.now());

        return this;
    }

    /**
     * Disable a feature flag.
     *
     * @return a Mono that completes when the feature flag has been disabled
     */
    public Feature disable() {
        this.description = new FeatureDescription(description.name(),
                description.description(),
                description.template(),
                false,
                description.createdAt(),
                LocalDateTime.now());

        return this;
    }


    /**
     * FeatureFlagId represents the id of a feature flag.
     */
    @Builder
    public record FeatureId(String featureKey) {
    }

    /**
     * FeatureFlagDescription represents the description of a feature flag.
     */
    @Builder
    public record FeatureDescription(String name, String description, FeatureTemplate template, boolean enabled,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
    }

    /**
     * FeatureConfigTemplate represents the template of a feature flag.
     */
    public record FeatureTemplate(DataType dataType, String key, String name, String description,
                                  List<FeatureTemplate> items) {
        /**
         * DataType represents the data type of a feature flag.
         */
        public enum DataType {
            BOOLEAN, STRING, NUMBER, OBJECT, ARRAY
        }
    }
}
