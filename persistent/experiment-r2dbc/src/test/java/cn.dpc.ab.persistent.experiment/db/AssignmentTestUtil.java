package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;

import java.util.Map;

/**
 * Test util for Assigment.git
 */

public class AssignmentTestUtil {

    /**
     * createAssignmentDb creates an AssignmentDb.
     *
     * @param experimentId the experiment id
     * @param clientId     the client id
     * @return the assignment db
     */
    public static AssignmentDb createAssignmentDb(String experimentId, String clientId) {
        Assignment assignment = Assignment.builder()
                .id(new Assignment.AssignmentId(new Experiment.ExperimentId(experimentId), clientId))
                .bucket(Experiment.Bucket.builder().key("bucketKey").build())
                .experimentConfigVersion(1L)
                .extraInfo(Map.of("key", "value"))
                .build();
        return AssignmentDb.from(assignment);
    }
}
