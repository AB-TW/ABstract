package cn.dpc.ab.persistent.filtercondition.db;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.core.FilterConditionTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.time.LocalDateTime;

/**
 * FilterConditionTemplateDb represents an filterCondition template.
 */
@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterConditionTemplateDb {
    @Version
    private long version;

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private String id;

    private String templateName;

    private FilterCondition.CustomAttributeCondition attributeCondition;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    /**
     * from converts Experiment to ExperimentDb.
     *
     * @param filterConditionTemplate the filterCondition template
     * @return the filterCondition template db
     */
    public static FilterConditionTemplateDb from(FilterConditionTemplate filterConditionTemplate) {
        return FilterConditionTemplateDb.builder()
                .id(filterConditionTemplate.getTemplateId())
                .templateName(filterConditionTemplate.getTemplateName())
                .attributeCondition(filterConditionTemplate.getAttributeCondition())
                .createdAt(filterConditionTemplate.getCreatedAt())
                .updatedAt(filterConditionTemplate.getUpdatedAt())
                .build();
    }

    /**
     * to converts FilterConditionTemplateDb to FilterConditionTemplate.
     *
     * @return the filterCondition Template
     */
    public static FilterConditionTemplate to(FilterConditionTemplateDb filterConditionTemplateDb) {
        return FilterConditionTemplate.builder()
                .templateId(filterConditionTemplateDb.getId())
                .templateName(filterConditionTemplateDb.getTemplateName())
                .attributeCondition(filterConditionTemplateDb.getAttributeCondition())
                .build();
    }

}
