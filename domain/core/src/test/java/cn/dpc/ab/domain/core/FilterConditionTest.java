package cn.dpc.ab.domain.core;

import cn.dpc.ab.domain.core.FilterCondition.CustomAttributeCondition;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static cn.dpc.ab.domain.core.FilterCondition.CustomAttributeCondition.Operator.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterConditionTest {


    @Test
    void version_match() {
        Map<String, String> map = Map.of("app_version", "9.1.1");
        CustomerId customerId = CustomerId.builder().customerId("111").clientId("222").attributes(map).build();
        FilterCondition filterCondition = FilterCondition.builder()
                .attributeConditions(List.of(new CustomAttributeCondition("test", "app_version",
                        VERSION_GREATER_THAN, "9.0.0", null)))
                .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                .build();
        assertTrue(filterCondition.match(customerId));
    }

    @Test
    void number_compare_match() {
        Map<String, String> attributes = Map.of("key1", "20", "key2", "30", "key3", "60");
        CustomerId customerId = CustomerId.builder().customerId("111").clientId("222").attributes(attributes).build();

        FilterCondition filterCondition = FilterCondition.builder()
                .attributeConditions(List.of(new CustomAttributeCondition("test1", "key1", GREATER_THAN,
                                "18", null),
                        new CustomAttributeCondition("test2", "key2", LESS_THAN, "50", null),
                        new CustomAttributeCondition("test3", "key3", EQUALS, "60", null)))
                .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                .build();
        assertTrue(filterCondition.match(customerId));


        assertFalse(filterCondition.match(CustomerId.builder()
                .customerId("111")
                .clientId("222")
                .attributes(Map.of("key1", "not a number"))
                .build()));
    }

    @Test
    void numeric_comparisons_should_use_number_ordering_for_multiple_digits() {
        Map<String, String> attributes = Map.of("age", "10", "score", "2", "limit", "10");
        CustomerId customerId = CustomerId.builder().customerId("111").clientId("222").attributes(attributes).build();

        FilterCondition filterCondition = FilterCondition.builder()
                .attributeConditions(List.of(
                        new CustomAttributeCondition("age", "age", GREATER_THAN_OR_EQUALS, "2", null),
                        new CustomAttributeCondition("score", "score", LESS_THAN, "10", null),
                        new CustomAttributeCondition("limit", "limit", LESS_THAN_OR_EQUALS, "10", null)))
                .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                .build();

        assertTrue(filterCondition.match(customerId));
    }

    @Test
    void gray_release_should_not_match_negative_hash_outside_percentage() {
        CustomerId customerId = CustomerId.builder()
                .customerId("customer-10")
                .clientId("client-10")
                .attributes(Map.of())
                .build();
        FilterCondition filterCondition = FilterCondition.builder()
                .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(1))
                .build();

        assertFalse(filterCondition.match(customerId));
    }

    @Nested
    class InMatchTest {
        @Test
        void should_in_match_return_true_when_attributes_match() {
            CustomerId customerId = CustomerId.builder()
                    .customerId("1-222")
                    .clientId("1-2223")
                    .attributes(Map.of("key1", "2222")).build();
            FilterCondition filterCondition = FilterCondition.builder()
                    .attributeConditions(List.of(
                            new CustomAttributeCondition("IN_TEST", "key1", IN,
                                    null, List.of("111", "2222"))
                    )).build();

            assertTrue(filterCondition.match(customerId));
        }

        @Test
        public void should_in_match_return_false_when_attributes_not_match() {
            CustomerId customerId = CustomerId.builder()
                    .customerId("1-222")
                    .clientId("1-2223")
                    .attributes(Map.of("key1", "333")).build();
            FilterCondition filterCondition = FilterCondition.builder()
                    .attributeConditions(List.of(
                            new CustomAttributeCondition("IN_TEST", "key1", IN, null, null)
                    )).build();

            assertFalse(filterCondition.match(customerId));
        }

        @Test
        public void should_in_match_return_false_when_items_is_null() {
            CustomerId customerId = CustomerId.builder()
                    .customerId("1-222")
                    .clientId("1-2223")
                    .attributes(Map.of("key1", "2222")).build();
            FilterCondition filterCondition = FilterCondition.builder()
                    .attributeConditions(List.of(
                            new CustomAttributeCondition("IN_TEST", "key1", IN,
                                    null, null)
                    )).build();

            assertFalse(filterCondition.match(customerId));
        }

        @Test
        public void should_in_match_return_false_when_items_is_empty() {
            CustomerId customerId = CustomerId.builder()
                    .customerId("1-222")
                    .clientId("1-2223")
                    .attributes(Map.of("key1", "2222")).build();
            FilterCondition filterCondition = FilterCondition.builder()
                    .attributeConditions(List.of(
                            new CustomAttributeCondition("IN_TEST", "key1", IN,
                                    null, List.of())
                    )).build();

            assertFalse(filterCondition.match(customerId));
        }
    }


    @Nested
    class NotInMatchTest {
        @Test
        void should_not_in_match_return_false_when_attributes_match() {
            CustomerId customerId = CustomerId.builder()
                    .customerId("1-222")
                    .clientId("1-2223")
                    .attributes(Map.of("key1", "2222")).build();
            FilterCondition filterCondition = FilterCondition.builder()
                    .attributeConditions(List.of(
                            new CustomAttributeCondition("IN_TEST", "key1", NOT_IN,
                                    null, List.of("111", "2222"))
                    )).build();

            assertFalse(filterCondition.match(customerId));
        }

        @Test
        public void should_not_in_match_return_true_when_attributes_not_match() {
            CustomerId customerId = CustomerId.builder()
                    .customerId("1-222")
                    .clientId("1-2223")
                    .attributes(Map.of("key1", "333")).build();
            FilterCondition filterCondition = FilterCondition.builder()
                    .attributeConditions(List.of(
                            new CustomAttributeCondition("IN_TEST", "key1", NOT_IN,
                                    null, null)
                    )).build();

            assertTrue(filterCondition.match(customerId));
        }

        @Test
        public void should_not_in_match_return_true_when_items_is_null() {
            CustomerId customerId = CustomerId.builder()
                    .customerId("1-222")
                    .clientId("1-2223")
                    .attributes(Map.of("key1", "2222")).build();
            FilterCondition filterCondition = FilterCondition.builder()
                    .attributeConditions(List.of(
                            new CustomAttributeCondition("IN_TEST", "key1", NOT_IN,
                                    null, null)
                    )).build();

            assertTrue(filterCondition.match(customerId));
        }

        @Test
        public void should_not_in_match_return_true_when_items_is_empty() {
            CustomerId customerId = CustomerId.builder()
                    .customerId("1-222")
                    .clientId("1-2223")
                    .attributes(Map.of("key1", "2222")).build();
            FilterCondition filterCondition = FilterCondition.builder()
                    .attributeConditions(List.of(
                            new CustomAttributeCondition("IN_TEST", "key1", NOT_IN,
                                    null, List.of())
                    )).build();

            assertTrue(filterCondition.match(customerId));
        }
    }

}
