package cn.dpc.ab.api.feature.dto;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.domain.feature.FeatureConfig.Status.DynamicStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * FeatureConfigResponse represents a feature config response.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureConfigResponse {
    private String id;

    private String featureKey;

    private String title;

    private String description;

    private String key;

    private Object data;

    private Map<String, String> saData;

    private DynamicStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private long priority;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private FilterCondition filterCondition;

    /**
     * from converts FeatureConfig to FeatureConfigResponse.
     *
     * @param featureConfig FeatureConfig
     * @return FeatureConfigResponse
     */
    public static FeatureConfigResponse from(FeatureConfig featureConfig) {
        return FeatureConfigResponse.builder()
                .id(featureConfig.getId().configId())
                .featureKey(featureConfig.getFeatureKey().featureKey())
                .title(featureConfig.getDescription().title())
                .description(featureConfig.getDescription().description())
                .key(featureConfig.getDescription().key())
                .data(featureConfig.getDescription().data())
                .saData(featureConfig.getDescription().saData())
                .status(featureConfig.getDescription().status().getStatus())
                .startTime(featureConfig.getDescription().status().getStartTime())
                .endTime(featureConfig.getDescription().status().getEndTime())
                .priority(featureConfig.getDescription().priority())
                .createdAt(featureConfig.getDescription().createdAt())
                .updatedAt(featureConfig.getDescription().updatedAt())
                .filterCondition(featureConfig.getDescription().filterCondition())
                .build();
    }
}
