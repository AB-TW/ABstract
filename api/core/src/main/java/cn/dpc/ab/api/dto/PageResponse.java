package cn.dpc.ab.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * PageResponse represents a page of data in the API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    List<T> content;

    @Builder.Default
    long total = 0;

    @Builder.Default
    int page = 0;

    @Builder.Default
    int pageSize = 10;

    public int getPage() {
        return total <= (long) page * pageSize ? 0 : page;
    }
}
