package cn.dpc.ab.api.feature.dto;

import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * FeatureConfigRequest represents a feature config request.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureConfigRequest {
    private String featureKey;

    private String title;

    private String description;

    private String key;

    private Object data;

    private Map<String, String> saData;

    private FeatureConfig.Status.StaticStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private long priority;

    /**
     * toFeatureConfig converts FeatureConfigRequest to FeatureConfig.
     *
     * @return FeatureConfig
     */
    public FeatureConfig toFeatureConfig() {
        return FeatureConfig.builder()
                .featureKey(Feature.FeatureId.builder().featureKey(featureKey).build())
                .description(FeatureConfig.Description.builder()
                        .title(title)
                        .description(description)
                        .key(key)
                        .data(data)
                        .saData(saData)
                        .status(FeatureConfig.Status.builder()
                                .staticStatus(status)
                                .startTime(startTime)
                                .endTime(endTime)
                                .build())
                        .priority(priority)
                        .build())
                .build();
    }
}
