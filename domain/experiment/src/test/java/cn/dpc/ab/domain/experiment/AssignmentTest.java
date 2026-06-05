package cn.dpc.ab.domain.experiment;

import cn.dpc.ab.domain.core.CustomerId;
import cn.dpc.ab.domain.core.FilterCondition;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void should_assign_negative_hash_customer_by_normalized_percentage() {
        Experiment experiment = generateCollectingExperiment("experiment-1");

        Assignment assignment = Assignment.createAssignment(experiment, CustomerId.builder()
                        .customerId("customer-100")
                        .clientId("client-100")
                        .attributes(Map.of())
                        .build())
                .block();

        assertEquals("rest", assignment.getDescription().bucket().key());
    }

    @Test
    public void should_preserve_positive_hash_bucket_when_normalizing_assignment_hash() {
        Experiment experiment = generateCollectingExperimentWithEvenBuckets("experiment-1");

        Assignment assignment = Assignment.createAssignment(experiment, CustomerId.builder()
                        .customerId("customer-10")
                        .clientId("client-10")
                        .attributes(Map.of())
                        .build())
                .block();

        assertEquals("second", assignment.getDescription().bucket().key());
    }

    @Test
    public void should_check_customer_id_when_extra_info_customer_ids_is_set() {
        Assignment assignment = Assignment.builder()
                .description(Assignment.Description.builder()
                        .extraInfo(Map.of("customerIds", Set.of("customer-1")))
                        .build())
                .build();

        assertTrue(assignment.containsCustomerId(CustomerId.builder()
                .customerId("customer-1")
                .clientId("client-1")
                .attributes(Map.of())
                .build()));
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

    private Experiment generateCollectingExperiment(String id) {
        return new Experiment(Experiment.ExperimentId.builder()
                .id(id)
                .build(),
                Experiment.Description.builder()
                        .name("name")
                        .description("description")
                        .status(Experiment.Status.builder()
                                .staticStatus(Experiment.Status.StaticStatus.PUBLISHED)
                                .build())
                        .featureKey("key")
                        .filterCondition(FilterCondition.builder()
                                .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                                .build())
                        .configVersion(0L)
                        .buckets(List.of(Experiment.Bucket.builder().key("first").name("first").percentage(1).build(),
                                Experiment.Bucket.builder().key("rest").name("rest").percentage(99).build()))
                        .build());
    }

    private Experiment generateCollectingExperimentWithEvenBuckets(String id) {
        return new Experiment(Experiment.ExperimentId.builder()
                .id(id)
                .build(),
                Experiment.Description.builder()
                        .name("name")
                        .description("description")
                        .status(Experiment.Status.builder()
                                .staticStatus(Experiment.Status.StaticStatus.PUBLISHED)
                                .build())
                        .featureKey("key")
                        .filterCondition(FilterCondition.builder()
                                .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                                .build())
                        .configVersion(0L)
                        .buckets(List.of(Experiment.Bucket.builder().key("first").name("first").percentage(50).build(),
                                Experiment.Bucket.builder().key("second").name("second").percentage(50).build()))
                        .build());
    }
}
