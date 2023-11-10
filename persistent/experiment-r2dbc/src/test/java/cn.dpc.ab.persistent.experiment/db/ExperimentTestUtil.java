package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.experiment.Experiment;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;


/**
 * ExperimentTestUtil provides a skeleton implementation of the ExperimentTestUtil interface.
 */
public class ExperimentTestUtil {

    /**
     * createExperimentDb creates an ExperimentDb.
     *
     * @param experimentId the experiment id
     * @return the experiment db
     */
    public static ExperimentDb createExperimentDb(String featureKey, String experimentId) {
        Experiment experiment = new Experiment(
                new Experiment.ExperimentId(null == experimentId ? UUID.randomUUID().toString() : experimentId),
                new Experiment.Description(
                        "name",
                        "description",
                        featureKey,
                        Experiment.Status.builder().staticStatus(Experiment.Status.StaticStatus.PUBLISHED)
                                .startTime(LocalDateTime.now().minusDays(1))
                                .endCollectTime(LocalDateTime.now().plusDays(1))
                                .endTime(LocalDateTime.now().plusDays(3))
                                .build(),
                        Collections.singletonList(Experiment.Bucket.builder().key("bucketKey").percentage(100).build()),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        new FilterCondition(),
                        0L)
        );
        return ExperimentDb.from(experiment);
    }
}
