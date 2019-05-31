package com.github.shk0da.demo.controller.v1;

import com.github.shk0da.demo.controller.ApiRoutes;
import com.github.shk0da.demo.model.PageableRequest;
import com.github.shk0da.demo.model.contacts.ContactGroupListModel;
import com.github.shk0da.demo.model.contacts.ContactGroupModel;
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

import static com.github.shk0da.demo.util.ExtractUtil.extractGroupId;
import static com.github.shk0da.demo.util.ResponseUtil.response;
import static java.lang.Math.min;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = {
        ApiRoutes.API_VERSION_1 + "/address-book/groups"
})
public class ContactGroupsController {

    private final ContactGroupsService contactGroupsService;

    @GetMapping
    @ApiOperation(value = "List contact group")
    @ApiImplicitParams({
            // Pageable
            @ApiImplicitParam(name = "page", value = "Indicates the page number to retrieve. Only positive number values are allowed. Default value is '1'", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "perPage", value = "Indicates the page size (number of items). If not specified, the value is '100' by default", dataType = "integer", paramType = "query"),
    })
    public ResponseEntity<ContactGroupListModel> getGroups(@RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                                           @RequestParam(value = "perPage", defaultValue = "250", required = false) Integer perPage) {
        Pageable pageable = new PageableRequest(page, min(perPage, 1000));
        return response(contactGroupsService.getPage(pageable));
    }

    @PostMapping
    @ApiOperation(value = "Create contact group")
    public ResponseEntity<ContactGroupModel> createGroup(@Valid @RequestBody ContactGroupModel contactGroup) {
        return response(contactGroupsService.create(contactGroup), HttpStatus.CREATED, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{groupId}")
    @ApiOperation(value = "Read contact group")
    @ApiImplicitParams({
            // Additional
            @ApiImplicitParam(name = "groupId", value = "Internal identifier of a contact group", dataType = "string", paramType = "path", required = true),
    })
    public ResponseEntity<ContactGroupModel> getGroup(@PathVariable("groupId") String groupId) {
        return response(contactGroupsService.getById(extractGroupId(groupId)));
    }

    @PutMapping("/{groupId}")
    @ApiOperation(value = "Update contact group")
    @ApiImplicitParams({
            // Additional
            @ApiImplicitParam(name = "groupId", value = "Internal identifier of a contact group", dataType = "string", paramType = "path", required = true),
    })
    public ResponseEntity<ContactGroupModel> updateGroup(@PathVariable("groupId") String groupId,
                                                         @Valid @RequestBody ContactGroupModel contactGroup) {
        return response(contactGroupsService.updateById(extractGroupId(groupId), contactGroup));
    }

    @RequestMapping(value = "/{groupId}", method = RequestMethod.PATCH)
    @ApiOperation(value = "Patch Contact Group")
    @ApiImplicitParams({
            // Additional
            @ApiImplicitParam(name = "groupId", value = "Internal identifier of a contact group", dataType = "string", paramType = "path", required = true),
    })
    public ResponseEntity<ContactGroupModel> patchGroup(@PathVariable("groupId") String groupId,
                                                        @RequestBody ContactGroupModel contactGroup) {
        contactGroup.patchValidation();
        return response(contactGroupsService.patchById(extractGroupId(groupId), contactGroup));
    }

    @DeleteMapping("/{groupId}")
    @ApiOperation(value = "Delete Contact Group")
    @ApiImplicitParams({
            // Additional
            @ApiImplicitParam(name = "groupId", value = "Internal identifier of a contact group", dataType = "string", paramType = "path", required = true),
    })
    public ResponseEntity deleteGroup(@PathVariable("groupId") String groupId) {
        contactGroupsService.delete(extractGroupId(groupId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
