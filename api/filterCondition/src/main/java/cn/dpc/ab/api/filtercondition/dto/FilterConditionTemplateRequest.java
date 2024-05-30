package cn.dpc.ab.api.filtercondition.dto;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.core.FilterConditionTemplate;
import lombok.*;

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

    private FilterCondition.CustomAttributeCondition attributeCondition;

    private String templateName;

    /**
     * create FilterConditionTemplate.
     */
    public FilterConditionTemplate to() {
        return FilterConditionTemplate.builder()
                .templateId(UUID.randomUUID().toString())
                .templateName(templateName)
                .attributeCondition(attributeCondition)
                .build();
    }
}
