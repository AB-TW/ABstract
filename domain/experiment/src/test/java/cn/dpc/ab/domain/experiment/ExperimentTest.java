package cn.dpc.ab.domain.experiment;

import cn.dpc.ab.domain.core.FilterCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExperimentTest {

    @Test
    public void should_new_experiment_throw_exception_when_buckets_is_empty() {
        assertThrows(IllegalArgumentException.class, () -> new Experiment(Experiment.Description.builder()
                .name("name")
                .description("description")
                .status(Experiment.Status.builder()
                        .staticStatus(Experiment.Status.StaticStatus.DRAFT)
                        .startTime(null)
                        .endTime(null)
                        .endCollectTime(null)
                        .build())
                .featureKey("featureKey")
                .filterCondition(FilterCondition.builder()
                        .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                        .build())
                .configVersion(0L)
                .buckets(List.of())
                .build()));
    }

    @Test
    public void should_new_experiment_throw_exception_when_buckets_is_null() {
        assertThrows(IllegalArgumentException.class, () -> new Experiment(Experiment.Description.builder()
                .name("name")
                .description("description")
                .status(Experiment.Status.builder()
                        .staticStatus(Experiment.Status.StaticStatus.DRAFT)
                        .startTime(null)
                        .endTime(null)
                        .endCollectTime(null)
                        .build())
                .featureKey("featureKey")
                .filterCondition(FilterCondition.builder()
                        .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                        .build())
                .configVersion(0L)
                .buckets(null)
                .build()));
    }

    @Test
    public void should_new_experiment_throw_exception_when_buckets_exist_duplicated_key() {
        assertThrows(IllegalArgumentException.class, () -> new Experiment(Experiment.Description.builder()
                .name("name")
                .description("description")
                .status(Experiment.Status.builder()
                        .staticStatus(Experiment.Status.StaticStatus.DRAFT)
                        .startTime(null)
                        .endTime(null)
                        .endCollectTime(null)
                        .build())
                .featureKey("featureKey")
                .filterCondition(FilterCondition.builder()
                        .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                        .build())
                .configVersion(0L)
                .buckets(List.of(Experiment.Bucket.builder().key("key1").name("name1").percentage(50).build(),
                        Experiment.Bucket.builder().key("key1").name("name2").percentage(50).build()))
                .build()));
    }

    @Test
    public void should_new_experiment_throw_exception_when_buckets_percentage_sum_is_not_100() {
        assertThrows(IllegalArgumentException.class, () -> new Experiment(Experiment.Description.builder()
                .name("name")
                .description("description")
                .status(Experiment.Status.builder()
                        .staticStatus(Experiment.Status.StaticStatus.DRAFT)
                        .startTime(null)
                        .endTime(null)
                        .endCollectTime(null)
                        .build())
                .featureKey("featureKey")
                .filterCondition(FilterCondition.builder()
                        .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                        .build())
                .configVersion(0L)
                .buckets(List.of(Experiment.Bucket.builder().key("key1").name("name1").percentage(50).build(),
                        Experiment.Bucket.builder().key("key2").name("name2").percentage(51).build()))
                .build()));
    }

    @Test
    public void should_new_experiment_throw_exception_when_sample_percentage_is_less_than_0() {
        assertThrows(IllegalArgumentException.class, () -> new Experiment(Experiment.Description.builder()
                .name("name")
                .description("description")
                .status(Experiment.Status.builder()
                        .staticStatus(Experiment.Status.StaticStatus.DRAFT)
                        .startTime(null)
                        .endTime(null)
                        .endCollectTime(null)
                        .build())
                .featureKey("featureKey")
                .filterCondition(FilterCondition.builder()
                        .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(-1))
                        .build())
                .configVersion(0L)
                .buckets(List.of(Experiment.Bucket.builder().key("key1").name("name1").percentage(50).build(),
                        Experiment.Bucket.builder().key("key2").name("name2").percentage(50).build()))
                .build()));
    }

    @Test
    public void should_new_experiment_throw_exception_when_sample_percentage_is_bigger_than_100() {
        assertThrows(IllegalArgumentException.class, () -> new Experiment(Experiment.Description.builder()
                .name("name")
                .description("description")
                .status(Experiment.Status.builder()
                        .staticStatus(Experiment.Status.StaticStatus.DRAFT)
                        .startTime(null)
                        .endTime(null)
                        .endCollectTime(null)
                        .build())
                .featureKey("featureKey")
                .filterCondition(FilterCondition.builder()
                        .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(101))
                        .build())
                .configVersion(0L)
                .buckets(List.of(Experiment.Bucket.builder().key("key1").name("name1").percentage(50).build(),
                        Experiment.Bucket.builder().key("key2").name("name2").percentage(50).build()))
                .build()));
    }

    @Test
    public void should_new_experiment_success() {
        new Experiment(Experiment.Description.builder()
                .name("name")
                .description("description")
                .status(Experiment.Status.builder()
                        .staticStatus(Experiment.Status.StaticStatus.DRAFT)
                        .startTime(null)
                        .endTime(null)
                        .endCollectTime(null)
                        .build())
                .featureKey("featureKey")
                .filterCondition(FilterCondition.builder()
                        .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(99))
                        .build())
                .configVersion(0L)
                .buckets(List.of(Experiment.Bucket.builder().key("key1").name("name1").percentage(50).build(),
                        Experiment.Bucket.builder().key("key2").name("name2").percentage(50).build()))
                .build());
    }


    @Test
    public void should_publish_success() {
        Experiment experiment = new Experiment(Experiment.Description.builder()
                .name("name")
                .description("description")
                .status(Experiment.Status.builder()
                        .staticStatus(Experiment.Status.StaticStatus.DRAFT)
                        .startTime(null)
                        .endTime(null)
                        .endCollectTime(null)
                        .build())
                .featureKey("featureKey")
                .filterCondition(FilterCondition.builder()
                        .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(99))
                        .build())
                .configVersion(0L)
                .buckets(List.of(Experiment.Bucket.builder().key("key1").name("name1").percentage(50).build(),
                        Experiment.Bucket.builder().key("key2").name("name2").percentage(50).build()))
                .build());

        experiment.publish();

        assertEquals(experiment.getDescription().status().staticStatus(), Experiment.Status.StaticStatus.PUBLISHED);
    }

    @Test
    public void should_publish_fail_when_status_is_stopped() {

        Experiment experiment = new Experiment(Experiment.Description.builder()
                .name("name")
                .description("description")
                .status(Experiment.Status.builder()
                        .staticStatus(Experiment.Status.StaticStatus.STOPPED)
                        .startTime(null)
                        .endTime(null)
                        .endCollectTime(null)
                        .build())
                .featureKey("featureKey")
                .filterCondition(FilterCondition.builder()
                        .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(99))
                        .build())
                .configVersion(0L)
                .buckets(List.of(Experiment.Bucket.builder().key("key1").name("name1").percentage(50).build(),
                        Experiment.Bucket.builder().key("key2").name("name2").percentage(50).build()))
                .build());

        assertThrows(IllegalStateException.class, experiment::publish);
    }

    @Test
    public void should_stop_success() {
        Experiment experiment = new Experiment(Experiment.Description.builder()
                .name("name")
                .description("description")
                .status(Experiment.Status.builder()
                        .staticStatus(Experiment.Status.StaticStatus.PUBLISHED)
                        .startTime(null)
                        .endTime(null)
                        .endCollectTime(null)
                        .build())
                .featureKey("featureKey")
                .filterCondition(FilterCondition.builder()
                        .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(99))
                        .build())
                .configVersion(0L)
                .buckets(List.of(Experiment.Bucket.builder().key("key1").name("name1").percentage(50).build(),
                        Experiment.Bucket.builder().key("key2").name("name2").percentage(50).build()))
                .build());

        experiment.stop();

        assertEquals(experiment.getDescription().status().staticStatus(), Experiment.Status.StaticStatus.STOPPED);
    }

    @Test
    public void should_pause_success() {
        Experiment experiment = new Experiment(Experiment.Description.builder()
                .name("name")
                .description("description")
                .status(Experiment.Status.builder()
                        .staticStatus(Experiment.Status.StaticStatus.PUBLISHED)
                        .startTime(null)
                        .endTime(null)
                        .endCollectTime(null)
                        .build())
                .featureKey("featureKey")
                .filterCondition(FilterCondition.builder()
                        .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(99))
                        .build())
                .configVersion(0L)
                .buckets(List.of(Experiment.Bucket.builder().key("key1").name("name1").percentage(50).build(),
                        Experiment.Bucket.builder().key("key2").name("name2").percentage(50).build()))
                .build());

        experiment.pause();

        assertEquals(experiment.getDescription().status().staticStatus(), Experiment.Status.StaticStatus.PAUSED);
    }

    @ParameterizedTest
    @EnumSource(value = Experiment.Status.StaticStatus.class, names = {"DRAFT", "PAUSED", "STOPPED"})
    public void should_pause_fail_when_status_not_published(Experiment.Status.StaticStatus staticStatus) {
        Experiment experiment = new Experiment(Experiment.Description.builder()
                .name("name")
                .description("description")
                .status(Experiment.Status.builder()
                        .staticStatus(staticStatus)
                        .startTime(null)
                        .endTime(null)
                        .endCollectTime(null)
                        .build())
                .featureKey("featureKey")
                .filterCondition(FilterCondition.builder()
                        .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(99))
                        .build())
                .configVersion(0L)
                .buckets(List.of(Experiment.Bucket.builder().key("key1").name("name1").percentage(50).build(),
                        Experiment.Bucket.builder().key("key2").name("name2").percentage(50).build()))
                .build());

        assertThrows(IllegalStateException.class, experiment::pause);
    }
}