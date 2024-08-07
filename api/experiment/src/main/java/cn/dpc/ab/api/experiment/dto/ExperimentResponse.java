package cn.dpc.ab.api.experiment.dto;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.experiment.Experiment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ExperimentRequest provides for experiment api.
 */
@Data
public class ExperimentResponse {

    private String id;

    private String name;

    private String description;

    private String featureKey;

    private Experiment.Status status;

    private List<Experiment.Bucket> buckets;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private FilterCondition filterCondition;

    /**
     * create ExperimentResponse from ExperimentDb.
     */
    public static ExperimentResponse from(Experiment experiment) {
        ExperimentResponse experimentResponse = new ExperimentResponse();
        BeanUtils.copyProperties(experiment.getDescription(), experimentResponse);
        experimentResponse.setId(experiment.getId().id());
        return experimentResponse;
    }
}
