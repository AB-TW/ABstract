package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.persistent.utils.JsonUtil;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ExperimentDb provides a skeleton implementation of the ExperimentDb interface.
 */
@Data
@Table("experiment")
public class ExperimentDb {
    private Long id;
    private String name;
    private String description;
    private String featureKey;
    private Experiment.Status.StaticStatus staticStatus;
    private LocalDateTime startTime;
    private LocalDateTime endCollectTime;
    private LocalDateTime endTime;
    private String buckets;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String filterCondition;
    private long configVersion;


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
        experimentDb.setStaticStatus(experiment.getDescription().status().staticStatus());
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
                                .staticStatus(this.staticStatus)
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
        this.staticStatus = description.status().staticStatus();
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
