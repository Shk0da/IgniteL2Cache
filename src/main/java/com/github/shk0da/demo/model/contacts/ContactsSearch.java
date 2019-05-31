package com.github.shk0da.demo.model.contacts;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.github.shk0da.demo.util.ExtractUtil.extractGroupIds;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactsSearch {

    /**
     * Search phrase to search all fields: ContactsService#allFieldsForSearch
     */
    private String searchString;

    /**
     * A list of IDs to search for groups
     */
    private List<Long> groupIds;

    public ContactsSearch(String searchString, List<String> groupIds) {
        this.searchString = searchString;
        this.groupIds = extractGroupIds(groupIds);
    }
}
