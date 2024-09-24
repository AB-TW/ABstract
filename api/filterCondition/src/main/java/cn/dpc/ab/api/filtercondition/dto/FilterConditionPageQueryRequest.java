package cn.dpc.ab.api.filtercondition.dto;

import cn.dpc.ab.api.dto.PageRequest;
import cn.dpc.ab.domain.core.FilterConditions;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * FilterConditionPageQueryRequest represents a page of filterCondition template in the API.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FilterConditionPageQueryRequest extends PageRequest {


    private String key;

    /**
     * Creates a FilterParams from a FeaturePageRequest.
     *
     * @return the FilterParams
     */
    public FilterConditions.FilterParams filterParams() {
        return FilterConditions.FilterParams.builder()
                .key(key)
                .build();
    }

}