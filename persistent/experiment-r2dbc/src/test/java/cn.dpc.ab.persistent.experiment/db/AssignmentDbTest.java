package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Assignment;
import cn.dpc.ab.domain.experiment.Experiment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class AssignmentDbTest {

    @Test
    void should_allow_assignment_without_bucket() {
        Assignment assignment = Assignment.builder()
                .id(new Assignment.AssignmentId(new Experiment.ExperimentId("1"), "client"))
                .description(Assignment.Description.builder()
                        .experimentConfigVersion(1L)
                        .build())
                .build();

        AssignmentDb assignmentDb = AssignmentDb.from(assignment);

        assertNull(assignmentDb.getBucketKey());
    }
}
