package cn.dpc.ab.api.filtercondition.dto;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.core.FilterConditionTemplate;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * FilterConditionTemplateResponse provides for filterCondition api.
 */
@Data
public class FilterConditionTemplateResponse {

    private String templateId;

    private String templateName;

    private List<FilterCondition.CustomAttributeCondition> attributeConditions;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * create FilterConditionTemplateResponse.
     */
    public static FilterConditionTemplateResponse from(FilterConditionTemplate filterConditionTemplate) {
        FilterConditionTemplateResponse filterConditionTemplateResponse = new FilterConditionTemplateResponse();
        filterConditionTemplateResponse.setAttributeConditions(filterConditionTemplate.getAttributeConditions());
        filterConditionTemplateResponse.setCreatedAt(filterConditionTemplate.getCreatedAt());
        filterConditionTemplateResponse.setUpdatedAt(filterConditionTemplate.getUpdatedAt());
        filterConditionTemplateResponse.setTemplateId(filterConditionTemplate.getTemplateId());
        filterConditionTemplateResponse.setTemplateName(filterConditionTemplate.getTemplateName());
        return filterConditionTemplateResponse;
    }

}
