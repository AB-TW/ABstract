package cn.dpc.ab.api.feature.dto;

import cn.dpc.ab.api.dto.PageRequest;
import cn.dpc.ab.domain.feature.Features.FilterParams;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FeaturePageRequest represents a page of features in the API.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FeaturePageRequest extends PageRequest {
    private String key;
    private Boolean enabled;

    /**
     * Creates a FilterParams from a FeaturePageRequest.
     *
     * @return the FilterParams
     */
    public FilterParams filterParams() {
        return FilterParams.builder()
                .key(key)
                .enabled(enabled)
                .build();
    }
}
