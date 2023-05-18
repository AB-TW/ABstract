package cn.dpc.ab.api.feature.dto;

import cn.dpc.ab.domain.feature.Feature;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * FeatureFlagResponse represents a feature flag in the API.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagResponse {
    private String featureKey;

    private String name;

    private String description;

    private boolean enabled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private Feature.FeatureTemplate template;

    /**
     * Creates a FeatureFlagResponse from a FeatureFlag.
     *
     * @param feature the FeatureFlag to convert
     * @return the FeatureFlagResponse
     */
    public static FeatureFlagResponse from(Feature feature) {
        return FeatureFlagResponse.builder()
                .featureKey(feature.getId().featureKey())
                .name(feature.getDescription().name())
                .description(feature.getDescription().description())
                .enabled(feature.getDescription().enabled())
                .createdAt(feature.getDescription().createdAt())
                .updatedAt(feature.getDescription().updatedAt())
                .template(feature.getDescription().template())
                .build();
    }
}
