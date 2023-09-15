package cn.dpc.ab.domain.experiment;

import cn.dpc.ab.domain.core.FilterCondition;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Experiment represents an experiment.
 */
@Getter
public class Experiment {
    private final ExperimentId id;
    private Description description;

    /**
     * Create a new experiment.
     *
     * @param description the description of the experiment
     */
    public Experiment(Description description) {
        this(null, description);
    }

    /**
     * Create a new experiment.
     *
     * @param id          the id of the experiment
     * @param description the description of the experiment
     */
    public Experiment(ExperimentId id, Description description) {
        this.id = Optional.ofNullable(id).orElse(ExperimentId.builder().id(UUID.randomUUID().toString()).build());
        this.description = description;
        validate();
    }


    /**
     * ExperimentId.
     */
    @Builder
    public record ExperimentId(String id) {
    }

    /**
     * Description represents the description of an experiment.
     */
    @Builder
    public record Description(String name, String description, String featureKey,
                              Status status,
                              List<Bucket> buckets,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              FilterCondition filterCondition,
                              long configVersion) {

    }

    /**
     * Bucket represents a bucket of an experiment.
     */
    @Builder
    public record Bucket(String key, String name, String description, Object config, int percentage) {
    }

    /**
     * Status represents the status of an experiment.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Status {
        private StaticStatus staticStatus;
        private LocalDateTime startTime;
        private LocalDateTime endCollectTime;
        private LocalDateTime endTime;

        public StaticStatus staticStatus() {
            return Optional.ofNullable(staticStatus).orElse(StaticStatus.DRAFT);
        }

        /**
         * StaticStatus.
         */
        public enum StaticStatus {
            DRAFT,
            PUBLISHED,
            PAUSED,
            STOPPED
        }

        /**
         * DynamicStatus.
         */
        public enum DynamicStatus {
            DRAFT,
            NOT_BEGIN,
            COLLECTING,
            FINISH_COLLECT,
            PAUSED,
            STOPPED
        }

        /**
         * Update the static status of the experiment.
         *
         * @param staticStatus the new static status of the experiment
         * @return the status of the experiment
         */
        public Status updateStatus(StaticStatus staticStatus) {
            this.staticStatus = staticStatus;
            return this;
        }

        /**
         * Get the dynamic status of the experiment.
         *
         * @return the dynamic status of the experiment
         */
        public DynamicStatus getStatus() {
            switch (staticStatus) {
                case PUBLISHED -> {
                    if (startTime != null && startTime.isAfter(LocalDateTime.now())) {
                        return DynamicStatus.NOT_BEGIN;
                    }
                    if (endTime != null && endTime.isBefore(LocalDateTime.now())) {
                        return DynamicStatus.STOPPED;
                    }
                    if (endCollectTime != null && endCollectTime.isBefore(LocalDateTime.now())) {
                        return DynamicStatus.FINISH_COLLECT;
                    }
                    return DynamicStatus.COLLECTING;
                }
                case PAUSED -> {
                    return DynamicStatus.PAUSED;
                }
                case STOPPED -> {
                    return DynamicStatus.STOPPED;
                }
                default -> {
                    return DynamicStatus.DRAFT;
                }
            }
        }
    }

    /**
     * Update the description of the experiment.
     *
     * @param description the new description of the experiment
     * @return the experiment
     */
    public Experiment update(Description description) {
        this.description = Description.builder()
                .buckets(description.buckets)
                .createdAt(this.description.createdAt)
                .updatedAt(LocalDateTime.now())
                .description(description.description)
                .featureKey(description.featureKey)
                .name(description.name)
                .status(description.status)
                .filterCondition(description.filterCondition)
                .configVersion(shouldUpdateConfigVersion(this.description, description)
                        ? this.description.configVersion + 1
                        : this.description.configVersion)
                .build();
        return this;
    }

    /**
     * Check should update the config version.
     *
     * @param oldDescription the old description
     * @param newDescription the new description
     */
    private boolean shouldUpdateConfigVersion(Description oldDescription, Description newDescription) {
        List<String> oldBucketKeys = oldDescription.buckets.stream().map(bucket -> bucket.key).toList();
        List<String> newBucketKeys = newDescription.buckets.stream().map(bucket -> bucket.key).toList();

        return !(new HashSet<>(oldBucketKeys).containsAll(newBucketKeys) && new HashSet<>(newBucketKeys).containsAll(oldBucketKeys))
                || oldDescription.buckets.stream().anyMatch(bucket ->
                newDescription.buckets.stream().anyMatch(newBucket -> newBucket.key.equals(bucket.key)
                        && newBucket.percentage != bucket.percentage))
                || !oldDescription.filterCondition.equals(newDescription.filterCondition);
    }

    /**
     * Publish the experiment.
     */
    public void publish() {
        if (!this.description.status.staticStatus().equals(Status.StaticStatus.DRAFT)
                && !this.description.status.staticStatus().equals(Status.StaticStatus.PAUSED)
                && !this.description.status.staticStatus().equals(Status.StaticStatus.PUBLISHED)) {
            throw new IllegalStateException("The experiment must be in draft status");
        }
        this.description.status.updateStatus(Status.StaticStatus.PUBLISHED);
    }

    /**
     * Pause the experiment.
     */
    public void pause() {
        if (!this.description.status.staticStatus().equals(Status.StaticStatus.PUBLISHED)) {
            throw new IllegalStateException("The experiment must be in published status");
        }
        this.description.status.updateStatus(Status.StaticStatus.PAUSED);
    }

    /**
     * Stop the experiment.
     */
    public void stop() {
        this.description.status.updateStatus(Status.StaticStatus.STOPPED);
    }

    /**
     * Validate the experiment.
     */
    private void validate() {
        validateBuckets();
    }

    /**
     * Validate the buckets of the experiment.
     */
    private void validateBuckets() {
        if (this.description.buckets == null || this.description.buckets.isEmpty()) {
            throw new IllegalArgumentException("The buckets of an experiment cannot be empty");
        }

        if (this.description.buckets.stream()
                .map(Bucket::key)
                .distinct()
                .anyMatch(element -> this.description.buckets.stream()
                        .filter(ele -> ele.key.equals(element)).count() > 1)) {
            throw new IllegalArgumentException("The buckets of an experiment cannot have duplicate keys");
        }


        int sum = this.description.buckets.stream()
                .mapToInt(Bucket::percentage)
                .sum();

        if (sum != 100) {
            throw new IllegalArgumentException("The sum of all bucket percentages must be 100");
        }
    }
}
