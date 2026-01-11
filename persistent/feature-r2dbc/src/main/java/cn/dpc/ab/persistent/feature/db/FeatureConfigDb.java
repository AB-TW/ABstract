package cn.dpc.ab.persistent.feature.db;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.persistent.r2dbc.ObjectAdapter;
import cn.dpc.ab.persistent.utils.JsonUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * FeatureConfigDb represents a feature config in database.
 */
@Getter
@Setter
@Table("feature_config")
public class FeatureConfigDb {
    @Id
    @Column("ID")
    private Long id;
    @Column("FEATURE_KEY")
    private String featureKey;
    @Column("CONFIG_KEY")
    private String key;
    @Column("TITLE")
    private String title;
    @Column("DESCRIPTION")
    private String description;
    @Column("DATA_ADAPTER")
    private String dataAdapter;
    @Column("DATA_ADAPTER_TYPE")
    private ObjectAdapter.DataType dataAdapterType;
    @Column("SA_DATA")
    private String saData;
    @Column("STATIC_STATUS")
    private String staticStatus;
    @Column("START_TIME")
    private LocalDateTime startTime;
    @Column("END_TIME")
    private LocalDateTime endTime;
    @Column("FILTER_CONDITION")
    private String filterCondition;
    @Column("PRIORITY")
    private long priority;
    @Column("CREATED_AT")
    private LocalDateTime createdAt;
    @Column("UPDATED_AT")
    private LocalDateTime updatedAt;

    /**
     * Set data.
     *
     * @param data the data to be set
     */
    @Transient
    public void setData(Object data) {
        ObjectAdapter objectAdapter = new ObjectAdapter(data);
        this.dataAdapter = objectAdapter.getData();
        this.dataAdapterType = objectAdapter.getType();
    }

    /**
     * Get data.
     *
     * @return the data
     */
    @Transient
    public Object getData() {
        return new ObjectAdapter(this.dataAdapterType, this.dataAdapter).toObject();
    }

    /**
     * Get static status as enum (for domain use only).
     * R2DBC uses the String field directly.
     */
    public FeatureConfig.Status.StaticStatus getStatusAsEnum() {
        if (staticStatus == null || staticStatus.trim().isEmpty()) {
            return FeatureConfig.Status.StaticStatus.DRAFT;
        }
        try {
            return FeatureConfig.Status.StaticStatus.valueOf(staticStatus);
        } catch (IllegalArgumentException e) {
            return FeatureConfig.Status.StaticStatus.DRAFT;
        }
    }

    /**
     * Set static status from enum (for domain use only).
     * R2DBC uses the String field directly.
     */
    public void setStatusFromEnum(FeatureConfig.Status.StaticStatus status) {
        this.staticStatus = status != null ? status.name() : null;
    }

    /**
     * Convert a FeatureConfig to FeatureConfigDb.
     *
     * @param featureConfig the featureConfig to convert
     * @return the FeatureConfigDb
     */
    public static FeatureConfigDb from(FeatureConfig featureConfig) {
        FeatureConfigDb featureConfigDb = new FeatureConfigDb();
        featureConfigDb.setId(Optional.ofNullable(featureConfig.getId())
                .map(FeatureConfig.FeatureConfigId::configId)
                .map(Long::valueOf)
                .orElse(null));
        featureConfigDb.setFeatureKey(featureConfig.getFeatureKey().featureKey());
        featureConfigDb.setTitle(featureConfig.getDescription().title());
        featureConfigDb.setKey(featureConfig.getDescription().key());
        featureConfigDb.setDescription(featureConfig.getDescription().description());
        featureConfigDb.setData(featureConfig.getDescription().data());
        featureConfigDb.setSaData(JsonUtil.toJson(featureConfig.getDescription().saData()));
        featureConfigDb.setStatusFromEnum(featureConfig.getDescription().status().getStaticStatus());
        featureConfigDb.setStartTime(featureConfig.getDescription().status().getStartTime());
        featureConfigDb.setEndTime(featureConfig.getDescription().status().getEndTime());
        featureConfigDb.setPriority(featureConfig.getDescription().priority());
        featureConfigDb.setCreatedAt(Optional.ofNullable(featureConfig.getDescription().createdAt()).orElse(LocalDateTime.now()));
        featureConfigDb.setUpdatedAt(Optional.ofNullable(featureConfig.getDescription().updatedAt()).orElse(LocalDateTime.now()));
        featureConfigDb.setFilterCondition(JsonUtil.toJson(featureConfig.getDescription().filterCondition()));
        return featureConfigDb;
    }

    /**
     * Convert a FeatureConfigDb to FeatureConfig.
     *
     * @return the FeatureConfig
     */
    public FeatureConfig to() {
        return FeatureConfig.builder()
                .id(Optional.ofNullable(id).map(item -> new FeatureConfig.FeatureConfigId(String.valueOf(item))).orElse(null))
                .featureKey(new Feature.FeatureId(this.featureKey))
                .description(new FeatureConfig.Description(
                        this.key,
                        this.title,
                        this.description,
                        this.getData(),
                        JsonUtil.fromJson(this.saData, Map.class),
                        new FeatureConfig.Status(
                                this.staticStatus != null
                                        ? FeatureConfig.Status.StaticStatus.valueOf(this.staticStatus)
                                        : FeatureConfig.Status.StaticStatus.DRAFT,
                                this.startTime,
                                this.endTime),
                        this.priority,
                        this.createdAt,
                        this.updatedAt,
                        JsonUtil.fromJson(this.filterCondition, FilterCondition.class)
                ))
                .build();
    }

    /**
     * Update description.
     *
     * @param description the description to be updated
     */
    public void updateDescription(FeatureConfig.Description description) {
        this.title = description.title();
        this.description = description.description();
        this.setData(description.data());
        this.setSaData(JsonUtil.toJson(description.saData()));
        this.setStatusFromEnum(description.status().getStaticStatus());
        this.startTime = description.status().getStartTime();
        this.endTime = description.status().getEndTime();
        this.filterCondition = JsonUtil.toJson(description.filterCondition());
        if (StringUtils.hasLength(description.key())) {
            this.key = description.key();
        }
        this.updatedAt = LocalDateTime.now();
        this.priority = description.priority();
    }
}
