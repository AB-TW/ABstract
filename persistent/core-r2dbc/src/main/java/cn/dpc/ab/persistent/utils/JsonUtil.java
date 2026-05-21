package cn.dpc.ab.persistent.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.List;

/**
 * JsonUtil is a utility class for json.
 */
public class JsonUtil {

    /**
     * The object mapper.
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Convert an object to json string.
     *
     * @param object the object to convert
     * @return the json string
     */
    @SneakyThrows
    public static String toJson(Object object) {
        if (null == object) {
            return null;
        }

        return objectMapper.writeValueAsString(object);
    }

    /**
     * Convert a json string to an object.
     *
     * @param jsonString the json string
     * @param clazz      the class of the object
     * @param <T>        the type of the object
     * @return the object
     */
    @SneakyThrows
    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        if (null == jsonString || jsonString.isEmpty()) {
            return null;
        }

        return objectMapper.readValue(jsonString, clazz);
    }

    /**
     * Convert a json array string to a typed list.
     *
     * @param jsonString the json string
     * @param clazz      the class of each list item
     * @param <T>        the type of each list item
     * @return the typed list
     */
    @SneakyThrows
    public static <T> List<T> fromJsonList(String jsonString, Class<T> clazz) {
        if (null == jsonString || jsonString.isEmpty()) {
            return null;
        }

        return objectMapper.readValue(jsonString,
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }
}
