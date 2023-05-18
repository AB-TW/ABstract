package cn.dpc.ab.api.feature.dto;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.Feature.FeatureTemplate;
import lombok.*;

/**
 * FeatureRequest represents a feature in the API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureRequest {
    private String featureKey;

    private String name;

    private String description;

    private boolean enabled;

    private FeatureTemplate template;


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
