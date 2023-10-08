package cn.dpc.ab.persistent.utils;

import org.springframework.data.domain.Sort;

/**
 * SqlUtil is a utility class for sql.
 */
public class SqlUtil {

    /**
     * toSortString converts a Sort object to a string.
     *
     * @param sort the Sort object
     * @return the string
     */
    public static String toSortString(Sort sort) {
        if (null == sort || sort.isEmpty()) {
            return "";
        }

        return String.join(", ", sort.stream()
                .map(s -> s.getProperty() + " " + s.getDirection())
                .toList());
    }
}
