package cn.dpc.ab.persistent.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

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
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Convert a json string to an object.
     *
     * @param template the json string
     * @param clazz    the class of the object
     * @param <T>      the type of the object
     * @return the object
     */
    @SneakyThrows
    public static <T> T fromJson(String template, Class<T> clazz) {
        return objectMapper.readValue(template, clazz);
    }
}
