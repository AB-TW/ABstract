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
     * Creates a Feature from a FeatureRequest.
     *
     * @return the Feature
     */
    public Feature toFeature() {
        return new Feature(
                new Feature.FeatureId(featureKey),
                Feature.Description.builder()
                        .name(name)
                        .description(description)
                        .enabled(enabled)
                        .template(template)
                        .build());
    }
}
