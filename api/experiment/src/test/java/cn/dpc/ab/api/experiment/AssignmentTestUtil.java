package cn.dpc.ab.api.experiment;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;

import java.util.List;

/**
 * AssignmentTestUtil represents the test util for assignments.
 */
public class AssignmentTestUtil {

    /**
     * createAssignment creates an assignment.
     *
     * @param clientId clientId
     * @param experimentId experimentId
     * @return Assignment
     */
    public static Assignment createAssignment(String clientId,
                                              String experimentId,
                                              String bucketKey,
                                              long experimentConfigVersion) {
        return Assignment.builder()
                .id(new Assignment.AssignmentId(
                        new Experiment.ExperimentId(experimentId), clientId
                ))
                .description(Assignment.Description.builder()
                        .bucket(Experiment.Bucket.builder().key(bucketKey).build())
                        .experimentConfigVersion(experimentConfigVersion)
                        .build())
                .build();
    }

    /**
     * createAssignment creates an assignment.
     *
     * @param id experimentId
     * @return Experiment
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
                        .featureKey("key")
                        .filterCondition(FilterCondition.builder()
                                .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(99))
                                .build())
                        .configVersion(0L)
                        .buckets(List.of(Experiment.Bucket.builder().key("key1").name("name1").percentage(50).build(),
                                Experiment.Bucket.builder().key("key2").name("name2").percentage(50).build()))
                        .build());
    }
}
