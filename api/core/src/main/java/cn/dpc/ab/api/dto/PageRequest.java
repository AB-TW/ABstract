package cn.dpc.ab.api.dto;

import lombok.Data;
import org.springframework.data.domain.Pageable;

/**
 * PageRequest represents a page of data in the API.
 */
@Data
public class PageRequest {
    private Integer page = 0;
    private Integer pageSize = 10;

    public void setPage(Integer page) {
        this.page = page == null || page < 0 ? 0 : page;
    }

    public long getFrom() {
        return Pageable.ofSize(pageSize).withPage(page).getOffset();
    }

    public long getTo() {
        return getFrom() + pageSize;
    }
}
