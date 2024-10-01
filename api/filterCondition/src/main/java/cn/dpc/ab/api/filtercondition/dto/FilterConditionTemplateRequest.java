package cn.dpc.ab.api.filtercondition.dto;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.core.FilterConditionTemplate;
import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 *  FilterConditionTemplateRequest provides for  FilterConditionTemplate api.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterConditionTemplateRequest {

    private List<FilterCondition.CustomAttributeCondition> attributeConditions;

    private String templateName;

    /**
     * create FilterConditionTemplate.
     */
    public FilterConditionTemplate to() {
        return FilterConditionTemplate.builder()
                .templateId(UUID.randomUUID().toString())
                .templateName(templateName)
                .attributeConditions(attributeConditions)
                .build();
    }
}
