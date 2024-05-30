package cn.dpc.ab.api.filtercondition.dto;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.core.FilterConditionTemplate;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * FilterConditionTemplateResponse provides for filterCondition api.
 */
@Data
public class FilterConditionTemplateResponse {

    private FilterCondition.CustomAttributeCondition attributeCondition;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * create FilterConditionTemplateResponse.
     */
    public static FilterConditionTemplateResponse from(FilterConditionTemplate filterConditionTemplate) {
        FilterConditionTemplateResponse filterConditionTemplateResponse = new FilterConditionTemplateResponse();
        filterConditionTemplateResponse.setAttributeCondition(filterConditionTemplate.getAttributeCondition());
        filterConditionTemplateResponse.setCreatedAt(LocalDateTime.now());
        filterConditionTemplateResponse.setUpdatedAt(LocalDateTime.now());
        return filterConditionTemplateResponse;
    }

}
