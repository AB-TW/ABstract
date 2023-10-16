package cn.dpc.ab.persistent.r2dbc;

import cn.dpc.ab.persistent.r2dbc.ObjectAdapter.DataType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ObjectAdapterTest {

    @Test
    public void should_adapt_null_object_to_string() {
        ObjectAdapter objectAdapter = new ObjectAdapter(null);
        assertEquals(DataType.STRING, objectAdapter.getType());
        assertNull(objectAdapter.getData());
    }

    @Test
    public void should_adapt_string_to_string() {
        ObjectAdapter objectAdapter = new ObjectAdapter("Test String");
        assertEquals(DataType.STRING, objectAdapter.getType());
        assertEquals("Test String", objectAdapter.getData());
    }

    @Test
    public void should_adapt_number_to_number() {
        ObjectAdapter objectAdapter = new ObjectAdapter(123);
        assertEquals(DataType.NUMBER, objectAdapter.getType());
        assertEquals("123", objectAdapter.getData());
    }

    @Test
    public void should_adapt_boolean_to_boolean() {
        ObjectAdapter objectAdapter = new ObjectAdapter(true);
        assertEquals(DataType.BOOLEAN, objectAdapter.getType());
        assertEquals("true", objectAdapter.getData());
    }

    @Test
    public void should_adapt_map_to_object() {
        ObjectAdapter objectAdapter = new ObjectAdapter(Map.of());
        assertEquals(DataType.OBJECT, objectAdapter.getType());
        assertEquals("{}", objectAdapter.getData());
    }

    @Test
    public void should_adapt_array_to_array() {
        ObjectAdapter objectAdapter = new ObjectAdapter(List.of());
        assertEquals(DataType.ARRAY, objectAdapter.getType());
        assertEquals("[]", objectAdapter.getData());
    }

}