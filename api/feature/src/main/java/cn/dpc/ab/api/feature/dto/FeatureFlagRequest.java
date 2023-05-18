package cn.dpc.ab.api.feature.dto;

import cn.dpc.ab.domain.feature.Feature;
import lombok.*;

/**
 * FeatureFlagRequest represents a feature flag in the API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureFlagRequest {
    private String featureKey;

    private String name;

    private String description;

    private boolean enabled;

    private Feature.FeatureTemplate template;


    /**
     * Creates a FeatureFlag from a FeatureFlagRequest.
     *
     * @return the FeatureFlag
     */
    public Feature toFeatureFlag() {
        return new Feature(
                new Feature.FeatureId(featureKey),
                Feature.FeatureDescription.builder()
                        .name(name)
                        .description(description)
                        .enabled(enabled)
                        .template(template)
                        .build());
    }
}
