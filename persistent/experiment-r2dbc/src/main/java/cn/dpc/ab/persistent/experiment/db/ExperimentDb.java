package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.persistent.utils.JsonUtil;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ExperimentDb provides a skeleton implementation of the ExperimentDb interface.
 */
@Data
@Table("experiment")
public class ExperimentDb {
    @Column("id")
    private Long id;
    @Column("name")
    private String name;
    @Column("description")
    private String description;
    @Column("feature_key")
    private String featureKey;
    @Column("static_status")
    private String staticStatus;
    @Column("start_time")
    private LocalDateTime startTime;
    @Column("end_collect_time")
    private LocalDateTime endCollectTime;
    @Column("end_time")
    private LocalDateTime endTime;
    @Column("buckets")
    private String buckets;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;
    @Column("filter_condition")
    private String filterCondition;
    @Column("config_version")
    private long configVersion;

    /**
     * Get static status as enum (for domain use only).
     * R2DBC uses the String field directly.
     */
    public Experiment.Status.StaticStatus getStaticStatusAsEnum() {
        if (staticStatus == null || staticStatus.trim().isEmpty()) {
            return Experiment.Status.StaticStatus.DRAFT;
        }
        try {
            return Experiment.Status.StaticStatus.valueOf(staticStatus);
        } catch (IllegalArgumentException e) {
            return Experiment.Status.StaticStatus.DRAFT;
        }
    }

    /**
     * Set static status from enum (for domain use only).
     * R2DBC uses the String field directly.
     */
    public void setStaticStatusFromEnum(Experiment.Status.StaticStatus status) {
        this.staticStatus = status != null ? status.name() : null;
    }


    /**
     * from creates a new ExperimentDb from an Experiment.
     *
     * @param experiment the Experiment to convert
     * @return the ExperimentDb
     */
    public static ExperimentDb from(Experiment experiment) {
        ExperimentDb experimentDb = new ExperimentDb();
        experimentDb.setId(Optional.ofNullable(experiment.getId())
                .map(Experiment.ExperimentId::id)
                .map(Long::valueOf)
                .orElse(null));
        experimentDb.setName(experiment.getDescription().name());
        experimentDb.setDescription(experiment.getDescription().description());
        experimentDb.setFeatureKey(experiment.getDescription().featureKey());
        experimentDb.setStaticStatusFromEnum(experiment.getDescription().status().staticStatus());
        experimentDb.setStartTime(experiment.getDescription().status().getStartTime());
        experimentDb.setEndCollectTime(experiment.getDescription().status().getEndCollectTime());
        experimentDb.setEndTime(experiment.getDescription().status().getEndTime());
        experimentDb.setBuckets(JsonUtil.toJson(experiment.getDescription().buckets()));
        experimentDb.setCreatedAt(Optional.ofNullable(experiment.getDescription().createdAt()).orElse(LocalDateTime.now()));
        experimentDb.setUpdatedAt(Optional.ofNullable(experiment.getDescription().updatedAt()).orElse(LocalDateTime.now()));
        experimentDb.setFilterCondition(JsonUtil.toJson(experiment.getDescription().filterCondition()));
        experimentDb.setConfigVersion(experiment.getDescription().configVersion());
        return experimentDb;
    }


    /**
     * to converts an ExperimentDb to an Experiment.
     *
     * @return the Experiment
     */
    public Experiment to() {
        return new Experiment(
                new Experiment.ExperimentId(String.valueOf(this.id)),
                new Experiment.Description(
                        this.name,
                        this.description,
                        this.featureKey,
                        Experiment.Status.builder()
                                .staticStatus(getStaticStatusAsEnum())
                                .startTime(this.startTime)
                                .endCollectTime(this.endCollectTime)
                                .endTime(this.endTime)
                                .build(),
                        JsonUtil.fromJson(buckets, List.class),
                        this.createdAt,
                        this.updatedAt,
                        JsonUtil.fromJson(this.filterCondition, FilterCondition.class),
                        configVersion
                )
        );
    }


    /**
     * updateDescription updates the description of an experiment.
     *
     * @param description the description to update
     */
    public void updateDescription(Experiment.Description description) {
        this.name = description.name();
        this.description = description.description();
        this.featureKey = description.featureKey();
        this.setStaticStatusFromEnum(description.status().staticStatus());
        this.startTime = description.status().getStartTime();
        this.endTime = description.status().getEndTime();
        this.endCollectTime = description.status().getEndCollectTime();
        this.buckets = JsonUtil.toJson(description.buckets());
        this.createdAt = description.createdAt();
        this.updatedAt = LocalDateTime.now();
        this.filterCondition = JsonUtil.toJson(description.filterCondition());
        this.configVersion = description.configVersion();
    }
}
