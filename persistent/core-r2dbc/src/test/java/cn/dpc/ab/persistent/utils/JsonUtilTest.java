package cn.dpc.ab.persistent.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonUtilTest {


    @Test
    void should_toJson_return_null_when_given_null_object() {
        assertNull(JsonUtil.toJson(null));
    }

    @Test
    void should_toJson_return_json_string_when_given_object() {
        assertEquals("{\"name\":\"dpc\"}", JsonUtil.toJson(new Person("dpc")));
    }

    @Test
    void should_toJson_return_array_json_string_when_given_object_array() {
        assertEquals("[{\"name\":\"dpc1\"},{\"name\":\"dpc2\"}]",
                JsonUtil.toJson(List.of(new Person("dpc1"), new Person("dpc2"))));
    }


    @Test
    void should_fromJson_return_null_when_given_null() {
        assertNull(JsonUtil.fromJson(null, Person.class));
    }

    @Test
    void should_fromJson_return_null_when_given_empty_string() {
        assertNull(JsonUtil.fromJson("", Person.class));
    }

    @Test
    void should_fromJson_return_object_when_given_json_string() {
        assertEquals("dpc", JsonUtil.fromJson("{\"name\":\"dpc\"}", Person.class).getName());
    }

    @Test
    void should_fromJson_return_object_array_when_given_json_string() {
        List<Map<String, String>> persons = JsonUtil.fromJson("[{\"name\":\"dpc1\"},{\"name\":\"dpc2\"}]", List.class);
        assertEquals("dpc1", persons.get(0).get("name"));
        assertEquals("dpc2", persons.get(1).get("name"));
    }

    @Data
    @NoArgsConstructor
    private static class Person {
        private String name;

        public Person(String name) {
            this.name = name;
        }
    }
}