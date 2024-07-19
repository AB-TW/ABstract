package cn.dpc.ab.api.experiment.dto;

import cn.dpc.ab.api.dto.PageRequest;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.domain.experiment.Experiments;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * ExperimentPageRequest represents a page of experiments in the API.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ExperimentPageRequest extends PageRequest {


    private String key;
    private String featureKey;
    private List<Experiment.Status.DynamicStatus> status;

    /**
     * Creates a FilterParams from a FeaturePageRequest.
     *
     * @return the FilterParams
     */
    public Experiments.FilterParams filterParams() {
        return Experiments.FilterParams.builder()
                .key(key)
                .featureKey(featureKey)
                .status(status)
                .build();
    }

}

