package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.persistent.experiment.db.ExperimentDb;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * ExperimentTestUtil provides a skeleton implementation of the ExperimentTestUtil interface.
 */
public class ExperimentTestUtil {

    /**
     * createExperimentDb creates an experiment.
     *
     * @param id          the id
     * @param experiment1 the experiment1
     * @return the experiment
     */
    public static ExperimentDb createExperimentDb(String id, String experiment1) {
        return ExperimentDb.builder()
                .id(null == id ? UUID.randomUUID().toString() : id)
                .name(experiment1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .buckets(List.of())
                .status(Experiment.Status.builder().staticStatus(Experiment.Status.StaticStatus.DRAFT).build())
                .samplePercentage(100)
                .featureKey("featureKey")
                .description("description")
                .build();
    }
}
