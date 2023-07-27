package cn.dpc.ab.persistent.feature.db;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.UNIQUE;

/**
 * FeatureConfigDB represents a feature in the database.
 */
@Document
@Data
public class FeatureConfigDb {
    @Version
    private long version;

    @Id
    @GeneratedValue(strategy = UNIQUE)
    private String id;

    @QueryIndexed
    private String featureKey;

    private Description description;

    public void updateDescription(FeatureConfig.Description description) {
        this.description = Description.from(description);
    }

    /**
     * Description represents a feature description in the database.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Description {
        private String key;
        private String title;
        private String description;
        private String stringData;
        private Object objData;
        private String templateData;
        private List<?> arrayData;
        private Map<String, String> saData;
        private FeatureConfig.Status status;
        private long priority;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private FilterCondition filterCondition;

        /**
         * setData sets the data field.
         *
         * @param data the data
         */
        public void setData(Object data) {
            if (null == data) {
                return;
            }

            if (data instanceof String) {
                this.stringData = String.valueOf(data);
            } else if (data instanceof Map<?, ?>) {
                this.objData = data;
            } else if (data instanceof List) {
                this.arrayData = (List<?>) data;
            }
        }

        /**
         * getData gets the data field.
         *
         * @return the data
         */
        public Object getData() {
            if (null != this.objData) {
                return objData;
            }

            if (null != this.arrayData) {
                return arrayData;
            }

            return this.stringData;
        }

        /**
         * from creates a Description from a Feature.Description.
         *
         * @param description the Feature.Description
         * @return the Description
         */
        public static Description from(FeatureConfig.Description description) {
            Description descriptionDb = Description.builder()
                    .title(description.title())
                    .key(Optional.ofNullable(description.key()).orElse(UUID.randomUUID().toString()))
                    .updatedAt(Optional.ofNullable(description.updatedAt()).orElse(LocalDateTime.now()))
                    .createdAt(Optional.ofNullable(description.createdAt()).orElse(LocalDateTime.now()))
                    .saData(description.saData())
                    .templateData(description.templateData())
                    .priority(description.priority())
                    .status(description.status())
                    .description(description.description())
                    .filterCondition(description.filterCondition())
                    .build();

            descriptionDb.setData(description.data());
            return descriptionDb;
        }

        /**
         * toFeatureConfigDescription converts a Description to a Feature.Description.
         *
         * @param description the Description
         * @return the Feature.Description
         */
        public static FeatureConfig.Description toFeatureConfigDescription(Description description) {
            return FeatureConfig.Description.builder()
                    .title(description.title)
                    .key(description.key)
                    .updatedAt(description.updatedAt)
                    .createdAt(description.createdAt)
                    .saData(description.saData)
                    .templateData(description.templateData)
                    .priority(description.priority)
                    .status(description.status)
                    .description(description.description)
                    .data(description.getData())
                    .filterCondition(description.filterCondition)
                    .build();
        }
    }

    /**
     * from creates a FeatureConfigDb from a FeatureConfig.
     *
     * @param featureConfig the FeatureConfig
     * @return the FeatureConfigDb
     */
    public static FeatureConfigDb from(FeatureConfig featureConfig) {
        FeatureConfigDb db = new FeatureConfigDb();
        db.setId(Optional.ofNullable(featureConfig.getId())
                .map(FeatureConfig.FeatureConfigId::configId)
                .orElse(UUID.randomUUID().toString()));
        db.setFeatureKey(featureConfig.getFeatureKey().featureKey());
        db.setDescription(Description.from(featureConfig.getDescription()));
        return db;
    }

    /**
     * toFeatureConfig converts a FeatureConfigDb to a FeatureConfig.
     *
     * @return the FeatureConfig
     */
    public FeatureConfig toFeatureConfig() {
        return FeatureConfig.builder()
                .id(new FeatureConfig.FeatureConfigId(id))
                .featureKey(new Feature.FeatureId(featureKey))
                .description(Description.toFeatureConfigDescription(description))
                .build();
    }
}
