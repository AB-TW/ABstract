package cn.dpc.ab.api.feature.dto;

import cn.dpc.ab.api.dto.PageRequest;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.domain.feature.Feature.Configs.FilterParams;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FeaturePageRequest represents a page of features in the API.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FeatureConfigPageRequest extends PageRequest {
    private String title;
    private FeatureConfig.Status.DynamicStatus status;

    /**
     * Creates a FilterParams from a FeaturePageRequest.
     *
     * @return the FilterParams
     */
    public FilterParams filterParams() {
        return FilterParams.builder()
                .title(title)
                .status(status)
                .build();
    }
}
