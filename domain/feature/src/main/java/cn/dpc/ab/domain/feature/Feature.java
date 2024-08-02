package cn.dpc.ab.domain.feature;

import lombok.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Feature represents a feature.
 */
@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class Feature {
    private FeatureId id;
    private Description description;

    @Setter
    private Configs configs;

    public Feature(FeatureId id, Description description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Enable a feature.
     *
     * @return a Mono that completes when the feature has been enabled
     */
    public Feature enable() {
        this.description = new Description(description.name(),
                description.description(),
                description.template(),
                true,
                description.createdAt(),
                LocalDateTime.now());

        return this;
    }

    /**
     * Update a feature.
     *
     * @param description the new description of the feature
     * @return a Mono that completes when the feature has been updated
     */
    public Feature update(Description description) {
        this.description = new Description(description.name(),
                description.description(),
                description.template(),
                description.enabled(),
                description.createdAt(),
                LocalDateTime.now());

        return this;
    }

    /**
     * Disable a feature.
     *
     * @return a Mono that completes when the feature has been disabled
     */
    public Feature disable() {
        this.description = new Description(description.name(),
                description.description(),
                description.template(),
                false,
                description.createdAt(),
                LocalDateTime.now());

        return this;
    }


    /**
     * FeatureId represents the id of a feature.
     */
    @Builder
    public record FeatureId(String featureKey) {

        /**
         * Validate FeatureId.
         */
        public FeatureId {
            if (featureKey == null || featureKey.isBlank()) {
                throw new IllegalArgumentException("featureKey cannot be null or blank");
            }
        }
    }

    /**
     * FeatureDescription represents the description of a feature.
     */
    @Builder
    public record Description(String name, String description, FeatureTemplate template, boolean enabled,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
    }

    /**
     * FeatureConfigTemplate represents the template of a feature.
     */
    @Builder
    public record FeatureTemplate(DataType dataType, String key, String name, String description,
                                  String defaultValue,
                                  Validator validator,
                                  Boolean keyParam,
                                  List<FeatureTemplate> items) {
        public static final FeatureTemplate DEFAULT = new FeatureTemplate(DataType.BOOLEAN, null, null,
                null, "false", null, false,  null);

        /**
         * DataType represents the data type of a feature.
         */
        public enum DataType {
            BOOLEAN, STRING, NUMBER, OBJECT, ARRAY
        }

        /**
         * Validator represents the validator of a feature.
         */
        @Builder
        public record Validator(boolean required, Integer maxLength, Integer max, Integer min, String pattern) {
        }
    }

    /**
     * FeatureConfigs represents the configs of a feature.
     */
    public interface Configs {

        /**
         * FilterParams represents the filter params of a feature.
         */
        @Builder
        record FilterParams(String title, List<FeatureConfig.Status.DynamicStatus> status) {
        }

        Flux<FeatureConfig> all(FilterParams params);

        Flux<FeatureConfig> subCollection(FilterParams params, long from, long to);

        Mono<Long> size(FilterParams params);

        Mono<FeatureConfig> get(FeatureConfig.FeatureConfigId id);

        Mono<FeatureConfig> add(FeatureConfig featureConfig);

        Mono<Void> update(FeatureConfig.FeatureConfigId id, FeatureConfig.Description description);

        Mono<Void> delete(FeatureConfig.FeatureConfigId id);

        Mono<Void> publish(FeatureConfig.FeatureConfigId id);

        Mono<Void> disable(FeatureConfig.FeatureConfigId id);

        Mono<Void> top(FeatureConfig.FeatureConfigId build);
    }
}
