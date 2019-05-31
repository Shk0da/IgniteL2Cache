package com.github.shk0da.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Contains navigation links for this collection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Navigation {

    /**
     * URI of the first page (page 1 for empty collection)
     */
    private String firstPage;

    /**
     * URI of the next page, if it exists
     */
    private String nextPage;

    /**
     * URI of the last page (page 1 for empty collection)
     */
    private String lastPage;

    /**
     * URI of the previous page, if it exists
     */
    private String previousPage;

    public static Navigation of(URI currentUri, Paging paging) {
        Navigation.NavigationBuilder builder = Navigation.builder();
        URI firstPage = ServletUriComponentsBuilder.fromUri(currentUri).queryParams(new LinkedMultiValueMap<String, String>() {{
            add("page", "1");
            add("perPage", String.valueOf(paging.getPerPage()));
        }}).build().toUri();
        builder.firstPage(firstPage.toString());

        URI lastPage = ServletUriComponentsBuilder.fromUri(currentUri).queryParams(new LinkedMultiValueMap<String, String>() {{
            add("page", String.valueOf(paging.getTotalPages() != null ? paging.getTotalPages() : 1));
            add("perPage", String.valueOf(paging.getPerPage()));
        }}).build().toUri();
        builder.lastPage(lastPage.toString());

        if (paging.getPage() != null && (paging.getTotalPages() == null || paging.getPage() < paging.getTotalPages())) {
            URI nextPage = ServletUriComponentsBuilder.fromUri(currentUri).queryParams(new LinkedMultiValueMap<String, String>() {{
                add("page", String.valueOf(paging.getPage() + 1));
                add("perPage", String.valueOf(paging.getPerPage()));
            }}).build().toUri();
            builder.nextPage(nextPage.toString());
        }

        if (paging.getPage() != null && paging.getPage() > 1) {
            URI previousPage = ServletUriComponentsBuilder.fromUri(currentUri).queryParams(new LinkedMultiValueMap<String, String>() {{
                add("page", String.valueOf(paging.getPage() - 1));
                add("perPage", String.valueOf(paging.getPerPage()));
            }}).build().toUri();
            builder.previousPage(previousPage.toString());
        }

        return builder.build();
    }
}
