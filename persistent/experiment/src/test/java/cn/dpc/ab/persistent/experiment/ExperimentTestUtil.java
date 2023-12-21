package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.core.FilterCondition;
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
                .filterCondition(FilterCondition.builder()
                        .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                        .build())
                .configVersion(0L)
                .featureKey("featureKey")
                .description("description")
                .build();
    }

    /**
     * createExperimentDb creates an experiment.
     *
     * @param id          the experiment id
     * @return the experiment
     */
    public static Experiment generateExperiment(String id) {
        return new Experiment(Experiment.ExperimentId.builder()
                .id(id)
                .build(),
                Experiment.Description.builder()
                        .name("name")
                        .description("description")
                        .status(Experiment.Status.builder()
                                .staticStatus(Experiment.Status.StaticStatus.DRAFT)
                                .startTime(null)
                                .endTime(null)
                                .endCollectTime(null)
                                .build())
                        .featureKey("featureKey")
                        .filterCondition(FilterCondition.builder()
                                .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(99))
                                .build())
                        .configVersion(0L)
                        .buckets(List.of(Experiment.Bucket.builder().key("bucketKey").percentage(100).build()))
                        .build());
    }
}
