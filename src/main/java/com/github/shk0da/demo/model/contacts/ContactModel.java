package com.github.shk0da.demo.model.contacts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.github.shk0da.demo.domain.Contact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactModel {

    private String id;
    private String groupId;
    private String groupName;
    private String firstName;
    private String lastName;
    private Date birthday;

    public static ContactModel of(Contact contact) {
        return ContactModel.builder()
                .id(contact.getId().toString())
                .groupId(contact.getContactGroup() != null ? contact.getContactGroup().getId().toString() : null)
                .groupName(contact.getContactGroup() != null ? contact.getContactGroup().getName() : null)
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .birthday(contact.getBirthday())
                .build();
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = StdDateFormat.DATE_FORMAT_STR_ISO8601)
    public Date getBirthday() {
        return birthday;
    }
}
