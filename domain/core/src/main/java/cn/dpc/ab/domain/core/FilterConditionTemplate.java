package cn.dpc.ab.domain.core;

import lombok.*;

import java.time.LocalDateTime;

/**
 * FilterConditionTemplate represents an filterCondition template.
 */
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
public class FilterConditionTemplate {

    private String templateId;

    private String templateName;

    private FilterCondition.CustomAttributeCondition attributeCondition;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

