package com.github.shk0da.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/**
 * Collection paging information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Paging {

    /**
     * Current page number, starts with 1 (may be null if non-existant page was requested)
     */
    private Integer page;

    /**
     * Number of records per page used to split whole collection into pages
     */
    private Integer perPage;

    /**
     * starts with 0 (index of the first record in this page)
     */
    private Integer pageStart;

    /**
     * Index of the last record in this page
     */
    private Integer pageEnd;

    /**
     * Total number of pages in a collection
     */
    private Integer totalPages;

    /**
     * Total number of elements in the whole collection
     */
    private Integer totalElements;

    public Paging(Integer totalElements) {
        this(totalElements, 1, totalElements);
    }

    public Paging(Integer totalElements, int page, int perPage) {
        this.totalElements = totalElements;
        this.perPage = perPage;
        this.totalPages = totalPages(totalElements, perPage);
        this.page = page > (totalPages != null ? totalPages : 0) ? null : page;

        int pageStart = (page - 1) * perPage;
        if (totalElements != null && pageStart < totalElements) {
            this.pageStart = pageStart;
            this.pageEnd = Math.min(pageStart + perPage - 1, totalElements - 1);
        }
    }

    public static Paging of(Page page) {
        return new Paging(Long.valueOf(page.getTotalElements()).intValue(), page.getNumber() + 1, page.getSize());
    }

    private Integer totalPages(Integer totalElements, int perPage) {
        if (totalElements == null) {
            return null;
        } else if (totalElements == 0) {
            return 1;
        }
        return totalElements / perPage + (totalElements % perPage == 0 ? 0 : 1);
    }
}
