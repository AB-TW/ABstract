package cn.dpc.ab.domain.feature;

import cn.dpc.ab.domain.core.FilterCondition;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * FeatureConfig represents a feature config.
 */
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FeatureConfig {

    public static final int HIGHEST_PRIORITY = -1;

    private FeatureConfigId id;

    // description
    private Description description;

    // associations
    private Feature.FeatureId featureKey;

    /**
     * FeatureConfigId represents the ID of a feature config.
     */
    @Builder
    public record FeatureConfigId(String configId) {
    }

    /**
     * Description represents the description of a feature config.
     */
    @Builder
    public record Description(String key,
                              String title,
                              String description,
                              Object data,
                              Map<String, String> saData,
                              Status status,
                              long priority,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              FilterCondition filterCondition) {
        public String key() {
            return Optional.ofNullable(this.key).filter(item -> !item.trim().isEmpty()).orElse(UUID.randomUUID().toString());
        }
    }

    /**
     * Status represents the status of a feature config.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Status {
        public Status updateStatus(StaticStatus staticStatus) {
            this.staticStatus = staticStatus;
            return this;
        }

        /**
         * StaticStatus represents the static status of a feature config.
         */
        public enum StaticStatus {
            DRAFT,
            PUBLISHED,
            DISABLED,
            DELETED
        }


        /**
         * DynamicStatus represents the dynamic status of a feature config.
         */
        public enum DynamicStatus {
            DRAFT,
            NOT_BEGIN,
            RUNNING,
            EXPIRED,
            DISABLED,
            DELETED
        }

        private StaticStatus staticStatus;

        private LocalDateTime startTime;

        private LocalDateTime endTime;


        /**
         * Get the static status of a feature config.
         *
         * @return the static status
         */
        public StaticStatus getStaticStatus() {
            return Optional.ofNullable(staticStatus).orElse(StaticStatus.DRAFT);
        }

        /**
         * Get the dynamic status of a feature config.
         *
         * @return the dynamic status
         */
        public DynamicStatus getStatus() {
            switch (staticStatus) {
                case PUBLISHED -> {
                    if (null != startTime && startTime.isAfter(LocalDateTime.now())) {
                        return DynamicStatus.NOT_BEGIN;
                    }

                    if (endTime != null && endTime.isBefore(LocalDateTime.now())) {
                        return DynamicStatus.EXPIRED;
                    }

                    return DynamicStatus.RUNNING;
                }
                case DISABLED -> {
                    return DynamicStatus.DISABLED;
                }
                case DELETED -> {
                    return DynamicStatus.DELETED;
                }
                default -> {
                    return DynamicStatus.DRAFT;
                }
            }
        }
    }


    /**
     * Update a new feature config.
     *
     * @param newDescription the new description
     * @return the feature config
     */
    public FeatureConfig update(Description newDescription) {
        this.description = Description.builder()
                .title(newDescription.title())
                .key(newDescription.key())
                .description(newDescription.description())
                .data(newDescription.data())
                .saData(newDescription.saData())
                .status(this.description.status)
                .createdAt(this.description.createdAt())
                .priority(this.description.priority)
                .updatedAt(LocalDateTime.now())
                .filterCondition(newDescription.filterCondition())
                .build();
        this.description.status.setStartTime(newDescription.status().getStartTime());
        this.description.status.setEndTime(newDescription.status().getEndTime());
        return this;
    }

    /**
     * Publish a feature config.
     *
     * @return the feature config
     */
    public FeatureConfig publish() {
        return updateStatus(Status.StaticStatus.PUBLISHED);
    }


    /**
     * Disable a feature config.
     *
     * @return the feature config
     */
    public FeatureConfig disable() {
        return updateStatus(Status.StaticStatus.DISABLED);
    }

    /**
     * Disable a feature config.
     *
     * @return the feature config
     */
    public FeatureConfig top() {
        this.description = Description.builder()
                .key(this.description.key())
                .title(this.description.title())
                .description(this.description.description())
                .data(this.description.data())
                .saData(this.description.saData())
                .status(this.description.status())
                .createdAt(this.description.createdAt())
                .priority(HIGHEST_PRIORITY)
                .filterCondition(this.description.filterCondition())
                .updatedAt(LocalDateTime.now())
                .build();
        return this;
    }

    /**
     * Delete a feature config.
     *
     * @return the feature config
     */
    public FeatureConfig delete() {
        return updateStatus(Status.StaticStatus.DELETED);
    }

    private FeatureConfig updateStatus(Status.StaticStatus status) {
        this.description = Description.builder()
                .key(this.description.key())
                .title(this.description.title())
                .description(this.description.description())
                .data(this.description.data())
                .saData(this.description.saData())
                .status(this.description.status.updateStatus(status))
                .createdAt(this.description.createdAt())
                .priority(this.description.priority)
                .filterCondition(this.description.filterCondition())
                .updatedAt(LocalDateTime.now())
                .build();
        return this;
    }
}