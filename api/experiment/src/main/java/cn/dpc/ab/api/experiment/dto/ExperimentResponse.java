package cn.dpc.ab.api.experiment.dto;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.persistent.experiment.db.ExperimentDb;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ExperimentRequest provides for experiment api.
 */
public class ExperimentResponse {

    private String id;

    private String name;

    private String description;

    private String featureKey;

    private Integer samplePercentage;

    private Experiment.Status status;

    private List<ExperimentDb.BucketDb> buckets;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private FilterCondition filterCondition;

    /**
     * create ExperimentResponse from ExperimentDb.
     */
    public static ExperimentResponse from(ExperimentDb experimentDb) {
        ExperimentResponse experimentResponse = new ExperimentResponse();
        BeanUtils.copyProperties(experimentDb, experimentResponse);
        return experimentResponse;
    }
}
