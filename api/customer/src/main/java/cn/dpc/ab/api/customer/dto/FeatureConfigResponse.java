package cn.dpc.ab.api.customer.dto;

import cn.dpc.ab.domain.feature.FeatureConfig;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * FeatureConfigResponse represents a feature config response.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class FeatureConfigResponse {
    public static final FeatureConfigResponse EMPTY = new FeatureConfigResponse();

    private String id;

    private String featureKey;

    private String key;

    private Object data;

    private Map<String, String> saData;

    private LocalDateTime expiredAt;

    private long priority;

    private LocalDateTime updatedAt;

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
                .key(featureConfig.getDescription().key())
                .data(featureConfig.getDescription().data())
                .saData(featureConfig.getDescription().saData())
                .expiredAt(featureConfig.getDescription().status().getEndTime())
                .priority(featureConfig.getDescription().priority())
                .updatedAt(featureConfig.getDescription().updatedAt())
                .build();
    }
}
