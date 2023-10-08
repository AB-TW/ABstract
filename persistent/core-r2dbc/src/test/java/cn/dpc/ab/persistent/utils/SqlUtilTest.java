package cn.dpc.ab.persistent.utils;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

class SqlUtilTest {

    @Test
    public void should_toSortString_return_empty_string_when_sort_is_null() {
        assertEquals("", SqlUtil.toSortString(null));
    }

    @Test
    public void should_toSortString_return_empty_string_when_sort_is_empty() {
        assertEquals("", SqlUtil.toSortString(Sort.unsorted()));
    }

    @Test
    public void should_toSortString_success() {
        var sort = Sort.by("updatedAt").descending().and(Sort.by("createdAt").ascending());
        assertEquals("updatedAt DESC, createdAt ASC", SqlUtil.toSortString(sort));
    }

}