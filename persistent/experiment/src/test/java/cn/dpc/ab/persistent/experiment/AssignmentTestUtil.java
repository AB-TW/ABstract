package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.persistent.experiment.db.AssignmentDb;

import java.time.LocalDateTime;

/**
 * AssignmentTestUtil provides a skeleton implementation of the AssignmentTestUtil interface.
 */
public class AssignmentTestUtil {

    /**
     * createAssignmentDb creates an AssignmentDb.
     *
     * @return the AssignmentDb
     */
    public static AssignmentDb createAssignmentDb(String clientId,
                                                  String experimentId,
                                                  long experimentConfigVersion,
                                                  String bucketKey) {
        return AssignmentDb.builder()
                .id("ci:" + clientId + "-" + "ei:" + experimentId)
                .clientId(clientId)
                .experimentId(experimentId)
                .bucketKey(bucketKey)
                .experimentConfigVersion(experimentConfigVersion)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
