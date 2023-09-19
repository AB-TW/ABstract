package cn.dpc.ab.persistent.experiment.db;

import cn.dpc.ab.domain.core.FilterCondition;
import cn.dpc.ab.domain.experiment.Experiment;
import cn.dpc.ab.persistent.couchbase.ObjectAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ExperimentDb represents an experiment.
 */
@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentDb {
    @Version
    private long version;

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private String id;

    private String name;

    private String description;

    private String featureKey;

    private Experiment.Status status;

    private List<BucketDb> buckets;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private FilterCondition filterCondition;

    private long configVersion;

    /**
     * toExperiment converts ExperimentDb to Experiment.
     *
     * @return the experiment
     */
    public Experiment toExperiment() {
        return new Experiment(
                new Experiment.ExperimentId(this.id),
                new Experiment.Description(
                        this.name,
                        this.description,
                        this.featureKey,
                        this.status,
                        this.buckets.stream().map(BucketDb::toBucket).collect(Collectors.toList()),
                        this.createdAt,
                        this.updatedAt,
                        this.filterCondition,
                        this.configVersion
                )
        );
    }

    /**
     * from converts Experiment to ExperimentDb.
     *
     * @param experiment the experiment
     * @return the experiment db
     */
    public static ExperimentDb from(Experiment experiment) {
        return ExperimentDb.builder()
                .id(Optional.ofNullable(experiment.getId()).map(Experiment.ExperimentId::id)
                        .orElse(UUID.randomUUID().toString()))
                .name(experiment.getDescription().name())
                .description(experiment.getDescription().description())
                .featureKey(experiment.getDescription().featureKey())
                .status(experiment.getDescription().status())
                .buckets(experiment.getDescription().buckets().stream().map(BucketDb::from).collect(Collectors.toList()))
                .createdAt(Optional.ofNullable(experiment.getDescription().createdAt()).orElse(LocalDateTime.now()))
                .updatedAt(Optional.ofNullable(experiment.getDescription().createdAt()).orElse(LocalDateTime.now()))
                .filterCondition(experiment.getDescription().filterCondition())
                .configVersion(experiment.getDescription().configVersion())
                .build();
    }


    /**
     * BucketDb represents bucket.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BucketDb {
        private String key;

        private String name;

        private String description;

        private ObjectAdapter configAdapter;

        private int percentage;

        /**
         * setConfig sets the data field.
         *
         * @param config the config
         */
        public void setConfig(Object config) {
            this.configAdapter = new ObjectAdapter(config);
        }

        /**
         * getData gets the data field.
         *
         * @return the data
         */
        public Object getConfig() {
            return Optional.ofNullable(this.configAdapter).map(ObjectAdapter::toObject).orElse(null);
        }

        /**
         * from converts Bucket to BucketDb.
         *
         * @param bucket the bucket
         * @return the bucket db
         */
        public static BucketDb from(Experiment.Bucket bucket) {
            BucketDb db = BucketDb.builder()
                    .key(bucket.key())
                    .name(bucket.name())
                    .description(bucket.description())
                    .percentage(bucket.percentage())
                    .build();

            db.setConfig(bucket.config());

            return db;
        }


        /**
         * toBucket converts BucketDb to Bucket.
         *
         * @return the bucket
         */
        public Experiment.Bucket toBucket() {
            return new Experiment.Bucket(
                    this.key,
                    this.name,
                    this.description,
                    this.getConfig(),
                    this.percentage
            );
        }
    }
}
