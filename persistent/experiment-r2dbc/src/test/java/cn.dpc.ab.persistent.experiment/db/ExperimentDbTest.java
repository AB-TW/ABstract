package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.experiment.Experiment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ExperimentDbTest {

    @Test
    void shouldLeaveIdEmptyForNewExperimentWithDomainGeneratedUuid() {
        Experiment experiment = newExperiment(null);

        ExperimentDb experimentDb = assertDoesNotThrow(() -> ExperimentDb.from(experiment));

        assertNull(experimentDb.getId());
    }

    @Test
    void shouldPreserveNumericDatabaseIdForExistingExperiment() {
        ExperimentDb experimentDb = ExperimentDb.from(newExperiment(new Experiment.ExperimentId("42")));

        assertEquals(42L, experimentDb.getId());
    }

    @Test
    void shouldRestoreBucketsAsDomainBuckets() {
        ExperimentDb experimentDb = ExperimentDb.from(newExperiment(new Experiment.ExperimentId("12")));

        Experiment experiment = experimentDb.to();

        assertEquals("bucket-a", experiment.getDescription().buckets().get(0).key());
    }

    private Experiment newExperiment(Experiment.ExperimentId id) {
        return new Experiment(id, Experiment.Description.builder()
                .name("experiment")
                .description("description")
                .featureKey("feature")
                .status(Experiment.Status.builder()
                        .staticStatus(Experiment.Status.StaticStatus.DRAFT)
                        .startTime(LocalDateTime.now().minusDays(1))
                        .endCollectTime(LocalDateTime.now().plusDays(1))
                        .endTime(LocalDateTime.now().plusDays(2))
                        .build())
                .buckets(List.of(Experiment.Bucket.builder()
                        .key("bucket-a")
                        .name("Bucket A")
                        .description("bucket description")
                        .config("config")
                        .percentage(100)
                        .build()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .filterCondition(new FilterCondition())
                .configVersion(0L)
                .build());
    }
}
