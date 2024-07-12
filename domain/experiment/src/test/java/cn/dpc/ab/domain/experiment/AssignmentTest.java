package cn.dpc.ab.domain.experiment;

import cn.dpc.ab.domain.core.FilterCondition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * AssignmentTest represents a test of assignment.
 */
public class AssignmentTest {

    @Test
    public void should_update_bucket_key_success() {
        Experiment experiment = generateExperiment("123");
        Assignment assignment = generateAssignment("111", "222");
        assignment.updateBucketKey(experiment, "key1");
        assertEquals("key1", assignment.getDescription().bucket().key());
    }

    @Test
    public void should_not_update_bucket_key_when_key_is_not_in_experiment_list() {
        Experiment experiment = generateExperiment("123");
        Assignment assignment = generateAssignment("111", "222");
        assertThrows(IllegalArgumentException.class,
                () -> assignment.updateBucketKey(experiment, "key10"));
    }

    private Assignment generateAssignment(String experimentId, String clientId) {
        return Assignment.builder()
                .id(new Assignment.AssignmentId(
                        new Experiment.ExperimentId(experimentId), clientId
                ))
                .description(Assignment.Description.builder()
                        .bucket(Experiment.Bucket.builder()
                                .key("oldKey")
                                .build())
                        .build())
                .build();
    }

    private Experiment generateExperiment(String id) {
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
