package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.experiment.Experiment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ExperimentDbTest {

    @Test
    void should_leave_database_id_empty_for_new_domain_experiment() {
        ExperimentDb experimentDb = ExperimentDb.from(new Experiment(description()));

        assertNull(experimentDb.getId());
    }

    @Test
    void should_preserve_numeric_database_id_for_existing_experiment() {
        Experiment experiment = new Experiment(new Experiment.ExperimentId("42"), description());

        ExperimentDb experimentDb = ExperimentDb.from(experiment);

        assertEquals(42L, experimentDb.getId());
    }

    private Experiment.Description description() {
        return Experiment.Description.builder()
                .name("name")
                .description("description")
                .featureKey("feature")
                .status(Experiment.Status.builder()
                        .staticStatus(Experiment.Status.StaticStatus.DRAFT)
                        .startTime(LocalDateTime.now())
                        .endCollectTime(LocalDateTime.now().plusHours(1))
                        .endTime(LocalDateTime.now().plusHours(2))
                        .build())
                .buckets(List.of(Experiment.Bucket.builder()
                        .key("bucket")
                        .percentage(100)
                        .build()))
                .build();
    }
}
