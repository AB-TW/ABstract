package cn.dpc.ab.domain.core;

import lombok.*;

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
                    .map(id -> id.hashCode() % 100 >= releasePercentage)
                    .orElse(false);
        }
    }


    /**
     * CustomAttributeCondition represents the custom attribute condition of a filter.
     */
    public record CustomAttributeCondition(String key, Operator operator, String value) {
        /**
         * Operator represents the operator of a custom attribute condition.
         */
        public enum Operator {
            EQUALS,
            NOT_EQUALS,
            NOT_CONTAINS,
            GREATER_THAN,
            GREATER_THAN_OR_EQUALS,
            LESS_THAN,
            LESS_THAN_OR_EQUALS,
            IN,
            NOT_IN
        }

        /**
         * Match returns true if the filter condition matches the customer.
         *
         * @param attributes the attributes of the customer
         * @return true if the filter condition matches the customer
         */
        public boolean match(Map<String, String> attributes) {
            // TODO: implement this method

            return false;
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
