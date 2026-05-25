package cn.dpc.ab.persistent.experiment;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.persistent.cache.LocalCacheManager;
import cn.dpc.ab.persistent.experiment.db.AssignmentDbRepository;
import cn.dpc.ab.persistent.experiment.db.AssignmentStatisticsRepository;
import cn.dpc.ab.persistent.experiment.db.ExperimentDb;
import cn.dpc.ab.persistent.experiment.db.ExperimentDbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * ExperimentsImplTest represents tests for ExperimentImpl.
 */
@ExtendWith(MockitoExtension.class)
public class ExperimentsImplTest {
    private static final String EXPERIMENT_ID = "experiment-1";

    @Mock
    private ExperimentDbRepository experimentDbRepository;

    @Mock
    private AssignmentDbRepository assignmentDbRepository;

    @Mock
    private AssignmentStatisticsRepository assignmentStatisticsRepository;

    @Mock
    private LocalCacheManager localCacheManager;

    private ExperimentsImpl experiments;

    @BeforeEach
    void setUp() {
        experiments = new ExperimentsImpl(experimentDbRepository, assignmentDbRepository,
                assignmentStatisticsRepository, localCacheManager);
    }

    @Test
    void should_reject_publish_stopped_experiment() {
        when(experimentDbRepository.findById(EXPERIMENT_ID))
                .thenReturn(Mono.just(experimentDb(Experiment.Status.StaticStatus.STOPPED)));

        experiments.publish(new Experiment.ExperimentId(EXPERIMENT_ID))
                .as(StepVerifier::create)
                .expectError(IllegalStateException.class)
                .verify();

        verify(experimentDbRepository, never()).save(any(ExperimentDb.class));
        verifyNoInteractions(localCacheManager);
    }

    @Test
    void should_reject_pause_draft_experiment() {
        when(experimentDbRepository.findById(EXPERIMENT_ID))
                .thenReturn(Mono.just(experimentDb(Experiment.Status.StaticStatus.DRAFT)));

        experiments.pause(new Experiment.ExperimentId(EXPERIMENT_ID))
                .as(StepVerifier::create)
                .expectError(IllegalStateException.class)
                .verify();

        verify(experimentDbRepository, never()).save(any(ExperimentDb.class));
        verifyNoInteractions(localCacheManager);
    }

    @Test
    void should_preserve_static_status_when_updating_experiment() {
        when(experimentDbRepository.findById(EXPERIMENT_ID))
                .thenReturn(Mono.just(experimentDb(Experiment.Status.StaticStatus.STOPPED)));
        when(experimentDbRepository.save(any(ExperimentDb.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(localCacheManager.clearCache(any()))
                .thenReturn(Mono.empty());

        experiments.update(new Experiment.ExperimentId(EXPERIMENT_ID), Experiment.Description.builder()
                        .name("Updated Experiment")
                        .description("Updated description")
                        .featureKey("feature-key")
                        .status(Experiment.Status.builder()
                                .staticStatus(Experiment.Status.StaticStatus.PUBLISHED)
                                .build())
                        .buckets(List.of(
                                Experiment.Bucket.builder().key("a").percentage(50).build(),
                                Experiment.Bucket.builder().key("b").percentage(50).build()))
                        .filterCondition(FilterCondition.builder()
                                .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                                .build())
                        .build())
                .as(StepVerifier::create)
                .verifyComplete();

        verify(experimentDbRepository).save(argThat(db ->
                db.getStatus().staticStatus().equals(Experiment.Status.StaticStatus.STOPPED)));
    }

    private ExperimentDb experimentDb(Experiment.Status.StaticStatus status) {
        return ExperimentDb.builder()
                .id(EXPERIMENT_ID)
                .name("Experiment")
                .description("Experiment description")
                .featureKey("feature-key")
                .status(Experiment.Status.builder()
                        .staticStatus(status)
                        .build())
                .buckets(List.of(
                        ExperimentDb.BucketDb.builder().key("a").percentage(50).build(),
                        ExperimentDb.BucketDb.builder().key("b").percentage(50).build()))
                .filterCondition(FilterCondition.builder()
                        .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                        .build())
                .configVersion(0L)
                .build();
    }
}
