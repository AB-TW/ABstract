package cn.dpc.ab.domain.core;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * FilterCondition represents the filter condition.
 */
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
public class FilterCondition {
    private GrayReleaseCondition grayReleaseCondition;

    private List<CustomAttributeCondition> attributeConditions;

    /**
     * GrayReleaseCondition represents the gray release condition of a filter.
     */
    public record GrayReleaseCondition(Integer releasePercentage) {

        /**
         * GrayReleaseCondition creates a new gray release condition.
         */
        public GrayReleaseCondition {
            if (releasePercentage == null || releasePercentage <= 0) {
                throw new IllegalArgumentException("The sample percentage must be greater than 0");
            }

            if (releasePercentage > 100) {
                throw new IllegalArgumentException("The sample percentage must be less than or equal to 100");
            }
        }

        /**
         * Match returns true if the filter condition matches the customer.
         *
         * @param clientId the client id
         * @return true if the filter condition matches the customer
         */
        public boolean match(String clientId) {
            if (null == releasePercentage) {
                return true;
            }

            return Optional.ofNullable(clientId)
                    .filter(id -> !id.isEmpty())
                    .map(id -> id.hashCode() % 100 < releasePercentage)
                    .orElse(false);
        }
    }


    /**
     * CustomAttributeCondition represents the custom attribute condition of a filter.
     */
    public record CustomAttributeCondition(String name, String key, Operator operator, String value) {
        /**
         * Operator represents the operator of a custom attribute condition.
         */
        public enum Operator {
            EQUALS,
            NOT_EQUALS,
            GREATER_THAN,
            GREATER_THAN_OR_EQUALS,
            LESS_THAN,
            LESS_THAN_OR_EQUALS,
            IN,
            NOT_IN,
            VERSION_GREATER_THAN,
            VERSION_GREATER_THAN_EQUALS,
            VERSION_LESS_THAN,
            VERSION_LESS_THAN_OR_EQUALS
        }

        /**
         * Match returns true if the filter condition matches the customer.
         *
         * @param attributes the attributes of the customer
         * @return true if the filter condition matches the customer
         */
        public boolean match(Map<String, String> attributes) {
            String attributeValue = attributes.get(key);
            if (attributeValue == null) {
                return false; // 属性不存在，不匹配
            }

            return switch (operator) {
                case EQUALS -> attributeValue.equals(value);
                case NOT_EQUALS -> !attributeValue.equals(value);
                case GREATER_THAN ->
                        StringUtils.isNumeric(attributeValue) && Double.parseDouble(attributeValue) > Double.parseDouble(value);
                case GREATER_THAN_OR_EQUALS -> StringUtils.isNumeric(attributeValue)  && attributeValue.compareTo(value) >= 0;
                case LESS_THAN -> StringUtils.isNumeric(attributeValue)  && attributeValue.compareTo(value) < 0;
                case LESS_THAN_OR_EQUALS -> StringUtils.isNumeric(attributeValue)  && attributeValue.compareTo(value) <= 0;
                case IN -> Arrays.asList(value.split(",")).contains(attributeValue);
                case NOT_IN -> !Arrays.asList(value.split(",")).contains(attributeValue);
                case VERSION_GREATER_THAN ->
                        new DefaultArtifactVersion(attributeValue).compareTo(new DefaultArtifactVersion(value)) > 0;
                case VERSION_GREATER_THAN_EQUALS ->
                        new DefaultArtifactVersion(attributeValue).compareTo(new DefaultArtifactVersion(value)) >= 0;
                case VERSION_LESS_THAN ->
                        new DefaultArtifactVersion(attributeValue).compareTo(new DefaultArtifactVersion(value)) < 0;
                case VERSION_LESS_THAN_OR_EQUALS ->
                        new DefaultArtifactVersion(attributeValue).compareTo(new DefaultArtifactVersion(value)) <= 0;
            };
        }
    }

    /**
     * Match returns true if the filter condition matches the customer.
     *
     * @param customerId the customer id
     * @return true if the filter condition matches the customer
     */
    public boolean match(CustomerId customerId) {
        return Stream.of(Optional.ofNullable(grayReleaseCondition)
                                .map(condition -> condition.match(customerId.clientId()))
                                .orElse(true),
                        Optional.ofNullable(attributeConditions)
                                .map(conditions -> conditions.stream()
                                        .allMatch(condition -> condition.match(customerId.attributes())))
                                .orElse(true))
                .allMatch(match -> match);
    }
}
