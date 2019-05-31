package com.github.shk0da.demo.model.contacts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.shk0da.demo.domain.Contact;
import com.github.shk0da.demo.model.Navigation;
import com.github.shk0da.demo.model.Paging;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactListModel {

    @Valid
    private List<ContactModel> records;
    private Navigation navigation;
    private Paging paging;

    public ContactListModel(@Valid List<ContactModel> records, Paging paging) {
        this.records = records;
        this.paging = paging;
        this.navigation = Navigation.of(fromCurrentRequest().build().toUri(), paging);
    }

    public static ContactListModel of(Page<Contact> page) {
        List<ContactModel> records = page.getContent()
                .stream()
                .map(ContactModel::of)
                .collect(Collectors.toList());
        Paging paging = Paging.of(page);
        Navigation navigation = Navigation.of(fromCurrentRequest().build().toUri(), paging);
        return new ContactListModel(records, navigation, paging);
    }

    public static ContactListModel of(List<Contact> contacts, Paging paging) {
        List<ContactModel> records = contacts
                .stream()
                .map(ContactModel::of)
                .collect(Collectors.toList());
        Navigation navigation = Navigation.of(fromCurrentRequest().build().toUri(), paging);
        return new ContactListModel(records, navigation, paging);
    }

    public static ContactListModel empty() {
        Paging paging = new Paging(0);
        Navigation navigation = Navigation.of(fromCurrentRequest().build().toUri(), paging);
        return new ContactListModel(Lists.newArrayList(), navigation, paging);
    }
}
