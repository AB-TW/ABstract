package cn.dpc.ab.domain.core;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static cn.dpc.ab.domain.core.FilterCondition.CustomAttributeCondition.Operator.*;
import static org.junit.jupiter.api.Assertions.*;

class FilterConditionTest {


    @Test
    void version_match() {
        HashMap<String, String> map = new HashMap<>();
        map.put("app_version", "9.1.1");
        CustomerId customerId = CustomerId.builder().customerId("111").clientId("222").attributes(map).build();
        FilterCondition filterCondition = FilterCondition.builder()
                .attributeConditions(List.of(new FilterCondition.CustomAttributeCondition("app_version",
                        VERSION_GREATER_THAN, "9.0.0")))
                .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                .build();
        assertTrue(filterCondition.match(customerId));
    }

    @Test
    void number_compare_match() {

        HashMap<String, String> map = new HashMap<>();
        map.put("key1", "20");
        map.put("key2", "30");
        map.put("key3", "60");
        CustomerId customerId = CustomerId.builder().customerId("111").clientId("222").attributes(map).build();
        FilterCondition filterCondition = FilterCondition.builder()
                .attributeConditions(List.of(new FilterCondition.CustomAttributeCondition("key1", GREATER_THAN, "18"),
                        new FilterCondition.CustomAttributeCondition("key2", LESS_THAN, "50"),
                        new FilterCondition.CustomAttributeCondition("key3", EQUALS, "60")))
                .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(100))
                .build();
        assertTrue(filterCondition.match(customerId));

        map.put("key1", "not a number");
        assertFalse(filterCondition.match(customerId));
    }
}
