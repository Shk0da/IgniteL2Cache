package com.github.shk0da.demo.controller.v1;

import com.github.shk0da.demo.controller.ApiRoutes;
import com.github.shk0da.demo.model.PageableRequest;
import com.github.shk0da.demo.model.contacts.ContactListModel;
import com.github.shk0da.demo.model.contacts.ContactsSearch;
import com.github.shk0da.demo.service.ContactsService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.github.shk0da.demo.util.ExtractUtil.extractSortDirection;
import static com.github.shk0da.demo.util.ExtractUtil.extractSortField;
import static com.github.shk0da.demo.util.ResponseUtil.response;
import static java.lang.Math.min;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = {
        ApiRoutes.API_VERSION_1 + "/address-book/contacts"
})
public class ContactsController {

    private final ContactsService contactsService;

    @GetMapping
    @ApiOperation(value = "Returns list of all contacts/contacts corresponding search criteria if any.")
    @ApiImplicitParams({
            // Additional
            @ApiImplicitParam(name = "searchString", value = "Search phrase to search all fields", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "groupId", value = "A list of IDs to search for groups (@see Type)", dataType = "array[string]", paramType = "query"),
            // Pageable
            @ApiImplicitParam(name = "sortBy", value = "Sorts results by the specified property. The default is 'first_name, last_name'", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sortDirection", value = "Sort direction to 'sortBy': [ASC, DESC]", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "Indicates the page number to retrieve. Only positive number values are allowed. Default value is '1'", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "perPage", value = "Indicates the page size (number of items). If not specified, the value is '100' by default", dataType = "integer", paramType = "query"),
    })
    public ResponseEntity<ContactListModel> searchContacts(@RequestParam(value = "searchString", required = false) String searchString,
                                                           @RequestParam(value = "groupId", required = false) List<String> groupId,
                                                           @RequestParam(value = "sortBy", required = false) String sortBy,
                                                           @RequestParam(value = "sortDirection", required = false) String sortDirection,
                                                           @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                                           @RequestParam(value = "perPage", defaultValue = "250", required = false) Integer perPage) {
        ContactsSearch search = new ContactsSearch(searchString, groupId);
        Sort sort = null;
        if (sortBy != null) {
            if (sortDirection != null) {
                sort = new Sort(extractSortDirection(sortDirection), uncapitalize(extractSortField(sortBy).name()));
            } else {
                sort = Sort.by(uncapitalize(extractSortField(sortBy).name()));
            }
        }
        Pageable pageable = new PageableRequest(page, min(perPage, 1000), sort);
        return response(contactsService.search(search, pageable));
    }
}
