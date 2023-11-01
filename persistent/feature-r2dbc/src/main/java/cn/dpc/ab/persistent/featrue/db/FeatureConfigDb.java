package cn.dpc.ab.persistent.featrue.db;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.feature.Feature;
import cn.dpc.ab.domain.feature.FeatureConfig;
import cn.dpc.ab.persistent.r2dbc.ObjectAdapter;
import cn.dpc.ab.persistent.utils.JsonUtil;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * FeatureConfigDb represents a feature config in database.
 */
@Data
@Table("feature_config")
public class FeatureConfigDb {
    @Id
    private Long id;
    private String featureKey;
    private String description;
    private String dataAdapter;
    @Column("config_key")
    private String key;
    private String title;
    private ObjectAdapter.DataType dataAdapterType;
    private String saData;
    private FeatureConfig.Status.StaticStatus staticStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String filterCondition;

    /**
     * Set data.
     *
     * @param data the data to be set
     */
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
    public Object getData() {
        return new ObjectAdapter(this.dataAdapterType, this.dataAdapter).toObject();
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
        featureConfigDb.setStaticStatus(featureConfig.getDescription().status().getStaticStatus());
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
                .id(new FeatureConfig.FeatureConfigId(String.valueOf(this.id)))
                .featureKey(new Feature.FeatureId(this.featureKey))
                .description(new FeatureConfig.Description(
                        this.key,
                        this.title,
                        this.description,
                        this.getData(),
                        JsonUtil.fromJson(this.saData, Map.class),
                        new FeatureConfig.Status(this.staticStatus, this.startTime, this.endTime),
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
        this.description = description.title();
        this.setData(description.data());
        this.setSaData(JsonUtil.toJson(description.saData()));
        this.staticStatus = description.status().getStaticStatus();
        this.startTime = description.status().getStartTime();
        this.endTime = description.status().getEndTime();
        this.filterCondition = JsonUtil.toJson(description.filterCondition());
        if (StringUtils.hasLength(description.key())) {
            this.key = description.key();
        }
        this.updatedAt = LocalDateTime.now();
    }
}
