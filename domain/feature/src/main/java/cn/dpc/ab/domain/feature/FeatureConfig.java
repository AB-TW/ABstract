package cn.dpc.ab.domain.feature;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * FeatureConfig represents a feature config.
 */
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FeatureConfig {

    private FeatureConfigId id;

    // description
    private Description description;

    // associations
    private Feature.FeatureId featureKey;

    /**
     * FeatureConfigId represents the ID of a feature config.
     */
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
                                           LocalDateTime updatedAt) {
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
                .description(newDescription.description())
                .data(newDescription.data())
                .saData(newDescription.saData())
                .status(this.description.status)
                .createdAt(this.description.createdAt())
                .priority(this.description.priority)
                .updatedAt(LocalDateTime.now())
                .build();
        return this;
    }

    /**
     * Publish a feature config.
     *
     * @return the feature config
     */
    public FeatureConfig publish() {
        this.description = Description.builder()
                .title(this.description.title())
                .description(this.description.description())
                .data(this.description.data())
                .saData(this.description.saData())
                .status(this.description.status.updateStatus(Status.StaticStatus.PUBLISHED))
                .createdAt(this.description.createdAt())
                .priority(this.description.priority)
                .updatedAt(LocalDateTime.now())
                .build();
        return this;
    }


    /**
     * Disable a feature config.
     *
     * @return the feature config
     */
    public FeatureConfig disable() {
        this.description = Description.builder()
                .title(this.description.title())
                .description(this.description.description())
                .data(this.description.data())
                .saData(this.description.saData())
                .status(this.description.status.updateStatus(Status.StaticStatus.DISABLED))
                .createdAt(this.description.createdAt())
                .priority(this.description.priority)
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
        this.description = Description.builder()
                .title(this.description.title())
                .description(this.description.description())
                .data(this.description.data())
                .saData(this.description.saData())
                .status(this.description.status.updateStatus(Status.StaticStatus.DELETED))
                .createdAt(this.description.createdAt())
                .priority(this.description.priority)
                .updatedAt(LocalDateTime.now())
                .build();
        return this;
    }
}