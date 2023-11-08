package cn.dpc.ab.domain.core;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static cn.dpc.ab.domain.core.FilterCondition.CustomAttributeCondition.Operator.VERSION_GREATER_THAN;
import static org.junit.jupiter.api.Assertions.*;

class FilterConditionTest {


    @Test
    void match() {
        FilterCondition filterCondition = FilterCondition.builder()
                .attributeConditions(List.of(new FilterCondition.CustomAttributeCondition("app_version",
                        VERSION_GREATER_THAN, "9.0.0")))
                .grayReleaseCondition(new FilterCondition.GrayReleaseCondition(10))
                .build();
        HashMap<String, String> map = new HashMap<>();
        map.put("app_version", "9.1.1");
        CustomerId customerId = CustomerId.builder().customerId("111").clientId("222").attributes(map).build();
        assertTrue(filterCondition.match(customerId));
    }
}
