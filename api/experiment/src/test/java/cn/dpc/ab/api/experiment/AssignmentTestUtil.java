package cn.dpc.ab.api.experiment;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;

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
                .bucket(Experiment.Bucket.builder().key(bucketKey).build())
                .experimentConfigVersion(experimentConfigVersion)
                .build();
    }
}
