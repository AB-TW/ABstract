package cn.dpc.ab.domain.core;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    private List<FilterCondition.CustomAttributeCondition> attributeConditions;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

