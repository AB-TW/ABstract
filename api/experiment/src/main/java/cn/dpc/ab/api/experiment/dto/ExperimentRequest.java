package cn.dpc.ab.api.experiment.dto;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.experiment.Experiment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ExperimentRequest provides for experiment api.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperimentRequest {

    private String name;

    private String description;

    private String featureKey;

    private Experiment.Status status;

    private List<Experiment.Bucket> buckets;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private FilterCondition filterCondition;

    /**
     * create ExperimentRequest from ExperimentDb.
     */
    public Experiment to() {
        return new Experiment(null, Experiment.Description.builder()
                .name(name)
                .description(description)
                .featureKey(featureKey)
                .filterCondition(filterCondition)
                .status(status)
                .buckets(buckets)
                .build());
    }

}
