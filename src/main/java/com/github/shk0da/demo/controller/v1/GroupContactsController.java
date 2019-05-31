package com.github.shk0da.demo.controller.v1;

import com.github.shk0da.demo.controller.ApiRoutes;
import com.github.shk0da.demo.model.PageableRequest;
import com.github.shk0da.demo.model.contacts.ContactListModel;
import com.github.shk0da.demo.model.contacts.ContactModel;
import com.github.shk0da.demo.service.ContactGroupsService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.github.shk0da.demo.util.ExtractUtil.extractContactId;
import static com.github.shk0da.demo.util.ExtractUtil.extractGroupId;
import static com.github.shk0da.demo.util.ResponseUtil.response;
import static java.lang.Math.min;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = {
        ApiRoutes.API_VERSION_1 + "/address-book/groups/{groupId}/contacts"
})
@ApiImplicitParams({
        // Group
        @ApiImplicitParam(name = "groupId", value = "Internal identifier of a contact group", required = true, dataType = "string", paramType = "path")
})
public class GroupContactsController {

    private final ContactGroupsService contactGroupsService;

    @GetMapping
    @ApiOperation(value = "Returns the group contacts")
    @ApiImplicitParams({
            // Pageable
            @ApiImplicitParam(name = "page", value = "Indicates the page number to retrieve. Only positive number values are allowed. Default value is '1'", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "perPage", value = "Indicates the page size (number of items). If not specified, the value is '100' by default", dataType = "integer", paramType = "query"),

    })
    public ResponseEntity<ContactListModel> getContacts(@PathVariable("groupId") String groupId,
                                                        @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                                        @RequestParam(value = "perPage", defaultValue = "250", required = false) Integer perPage) {
        Pageable pageable = new PageableRequest(page, min(perPage, 1000));
        return response(contactGroupsService.getContacts(extractGroupId(groupId), pageable));
    }

    @PostMapping
    @ApiOperation(value = "Adds new contact to the group")
    public ResponseEntity<ContactModel> addContact(@PathVariable("groupId") String groupId,
                                                   @Valid @RequestBody ContactModel contactModel) {
        return response(contactGroupsService.addContact(extractGroupId(groupId), contactModel), HttpStatus.CREATED, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{contactId}")
    @ApiOperation(value = "Returns the contact by ID")
    @ApiImplicitParams({
            // Additional
            @ApiImplicitParam(name = "contactId", value = "Internal identifier of a contact", dataType = "string", paramType = "path", required = true),
    })
    public ResponseEntity<ContactModel> getContact(@PathVariable("groupId") String groupId,
                                                   @PathVariable("contactId") String contactId) {
        return response(contactGroupsService.getContact(extractGroupId(groupId), extractContactId(contactId)));
    }

    @PutMapping("/{contactId}")
    @ApiOperation(value = "Updates (replaces) the contact. All the fields updated according to the request body")
    @ApiImplicitParams({
            // Additional
            @ApiImplicitParam(name = "contactId", value = "Internal identifier of a contact", dataType = "string", paramType = "path", required = true),
    })
    public ResponseEntity<ContactModel> updateContact(@PathVariable("groupId") String groupId,
                                                      @PathVariable("contactId") String contactId,
                                                      @Valid @RequestBody ContactModel contactModel) {
        return response(contactGroupsService.updateContact(extractGroupId(groupId), extractContactId(contactId), contactModel));
    }

    @RequestMapping(value = "/{contactId}", method = RequestMethod.PATCH)
    @ApiOperation(value = "Updates (partially) the contact. Only fields specified in the request body updated")
    @ApiImplicitParams({
            // Additional
            @ApiImplicitParam(name = "contactId", value = "Internal identifier of a contact", dataType = "string", paramType = "path", required = true),
    })
    public ResponseEntity<ContactModel> patchContact(@PathVariable("groupId") String groupId,
                                                     @PathVariable("contactId") String contactId,
                                                     @Valid @RequestBody ContactModel contactModel) {
        return response(contactGroupsService.patchContact(extractGroupId(groupId), extractContactId(contactId), contactModel));
    }

    @DeleteMapping("/{contactId}")
    @ApiOperation(value = "Removes the contact")
    @ApiImplicitParams({
            // Additional
            @ApiImplicitParam(name = "contactId", value = "Internal identifier of a contact", dataType = "string", paramType = "path", required = true),
    })
    public ResponseEntity deleteContact(@PathVariable("groupId") String groupId,
                                        @PathVariable("contactId") String contactId) {
        contactGroupsService.deleteContact(extractGroupId(groupId), extractContactId(contactId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
