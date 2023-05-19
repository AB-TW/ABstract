package cn.dpc.ab.api.feature.dto;

import cn.dpc.ab.domain.feature.Feature;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * FeatureResponse represents a feature in the API.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureResponse {
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
     * Creates a FeatureResponse from a Feature.
     *
     * @param feature the Feature to convert
     * @return the FeatureResponse
     */
    public static FeatureResponse from(Feature feature) {
        return FeatureResponse.builder()
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
