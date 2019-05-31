package com.github.shk0da.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Site {

    /**
     * Site extension identifier, if the user extension belongs to any site,
     * main-site otherwise.
     */
    private String id;

    /**
     * site extension name, if the user extension belongs to any site,
     * "Main Site" (localized) otherwise.
     */
    private String name;
}
