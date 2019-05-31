package com.github.shk0da.demo.service;

import com.github.shk0da.demo.domain.Contact;
import com.github.shk0da.demo.domain.ContactGroup;
import com.github.shk0da.demo.exception.DemoException;
import com.github.shk0da.demo.exception.ErrorCode;
import com.github.shk0da.demo.model.Paging;
import com.github.shk0da.demo.model.contacts.ContactGroupListModel;
import com.github.shk0da.demo.model.contacts.ContactGroupModel;
import com.github.shk0da.demo.model.contacts.ContactListModel;
import com.github.shk0da.demo.model.contacts.ContactModel;
import com.github.shk0da.demo.repository.ContactGroupRepository;
import com.github.shk0da.demo.repository.ContactRepository;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ContactGroupsService {

    private final ContactRepository contactRepository;
    private final ContactGroupRepository contactGroupRepository;

    /**
     * Create contact group
     *
     * @param contactGroup {@link ContactGroupModel}
     * @return {@link ContactGroupModel}
     */
    @Retryable(value = {SQLException.class}, maxAttempts = 4, backoff = @Backoff(500))
    public ContactGroupModel create(ContactGroupModel contactGroup) {
        ContactGroup group = contactGroupRepository.save(ContactGroup.of(contactGroup));
        return ContactGroupModel.of(group);
    }

    /**
     * Page contact group
     *
     * @return {@link ContactGroupListModel}
     */
    public ContactGroupListModel getPage(Pageable pageable) {
        Page<ContactGroup> page = contactGroupRepository.findAll(pageable);
        List<ContactGroupModel> records = page.getContent().stream().map(ContactGroupModel::of).collect(Collectors.toList());
        return new ContactGroupListModel(records, Paging.of(page));
    }

    /**
     * Get contact group by ID
     *
     * @param groupId group ID
     * @return {@link ContactGroupModel}
     */
    public ContactGroupModel getById(Long groupId) {
        ContactGroup group = getContactGroup(groupId);
        return new ContactGroupModel(group.getId(), group.getName(), group.getDescription());
    }

    /**
     * Update contact group by ID
     *
     * @param groupId      group ID
     * @param contactGroup {@link ContactGroupModel}
     * @return {@link ContactGroupModel}
     */
    public ContactGroupModel updateById(Long groupId, ContactGroupModel contactGroup) {
        ContactGroup group = getContactGroup(groupId);
        group.setName(contactGroup.getName());
        group.setDescription(contactGroup.getDescription());
        group = contactGroupRepository.save(group);
        return new ContactGroupModel(group.getId(), group.getName(), group.getDescription());
    }

    /**
     * Patch contact group by ID
     *
     * @param groupId      group ID
     * @param contactGroup {@link ContactGroupModel}
     * @return {@link ContactGroupModel}
     */
    public ContactGroupModel patchById(Long groupId, ContactGroupModel contactGroup) {
        ContactGroup group = getContactGroup(groupId);
        if (contactGroup.getName() != null) {
            group.setName(contactGroup.getName());
        }
        if (contactGroup.getDescription() != null) {
            group.setDescription(contactGroup.getDescription());
        }
        group = contactGroupRepository.save(group);
        return new ContactGroupModel(group.getId(), group.getName(), group.getDescription());
    }

    /**
     * Delete contact group by ID
     *
     * @param groupId group ID
     */
    @Retryable(value = {SQLException.class}, maxAttempts = 4, backoff = @Backoff(500))
    public void delete(Long groupId) {
        ContactGroup group = getContactGroup(groupId);
        contactGroupRepository.delete(group);
        contactRepository.deleteAllByContactGroup(group);
    }

    /**
     * Group contacts retrieval
     *
     * @param groupId  group ID
     * @param pageable {@link Pageable}
     * @return {@link ContactListModel}
     */
    public ContactListModel getContacts(Long groupId, Pageable pageable) {
        ContactGroup group = getContactGroup(groupId);
        Page<Contact> contactPage = contactRepository.findAllByContactGroup(group, pageable);
        return ContactListModel.of(contactPage);
    }

    /**
     * Group contact Create
     *
     * @param groupId      group ID
     * @param contactModel {@link ContactModel}
     * @return {@link ContactModel}
     */
    @Retryable(value = {SQLException.class}, maxAttempts = 4, backoff = @Backoff(500))
    public ContactModel addContact(Long groupId, ContactModel contactModel) {
        ContactGroup group = getContactGroup(groupId);
        Contact contact = contactRepository.save(Contact.of(contactModel, group));
        return ContactModel.of(contact);
    }

    /**
     * Get group contact by ID
     *
     * @param groupId   group ID
     * @param contactId contact ID
     * @return {@link ContactModel}
     */
    public ContactModel getContact(Long groupId, Long contactId) {
        ContactGroup group = getContactGroup(groupId);
        Contact contact = getGroupContact(contactId, group);
        return ContactModel.of(contact);
    }

    /**
     * Update group contact by ID
     *
     * @param groupId      group ID
     * @param contactId    contact ID
     * @param contactModel {@link ContactModel}
     * @return {@link ContactModel}
     */
    @Retryable(value = {SQLException.class}, maxAttempts = 4, backoff = @Backoff(500))
    public ContactModel updateContact(Long groupId, Long contactId, ContactModel contactModel) {
        ContactGroup group = getContactGroup(groupId);
        Contact contact = getGroupContact(contactId, group);
        Contact contactUpdate = Contact.of(contactModel, group);
        contactUpdate.setId(contact.getId());
        contactUpdate.setVersion(contact.getVersion());
        contact = contactRepository.save(contactUpdate);
        return ContactModel.of(contact);
    }

    /**
     * Patch group contact by ID
     *
     * @param groupId      group ID
     * @param contactId    contact ID
     * @param contactModel {@link ContactModel}
     * @return {@link ContactModel}
     */
    @Retryable(value = {SQLException.class}, maxAttempts = 4, backoff = @Backoff(500))
    public ContactModel patchContact(Long groupId, Long contactId, ContactModel contactModel) {
        ContactGroup group = getContactGroup(groupId);
        Contact contact = getGroupContact(contactId, group);
        Contact contactPatch = Contact.of(contactModel, group);
        contact.path(contactPatch);
        return ContactModel.of(contactRepository.save(contact));
    }

    /**
     * Delete group contact by ID
     *
     * @param groupId   group ID
     * @param contactId contact ID
     */
    @Retryable(value = {SQLException.class}, maxAttempts = 4, backoff = @Backoff(500))
    public void deleteContact(Long groupId, Long contactId) {
        ContactGroup group = getContactGroup(groupId);
        Contact contact = getGroupContact(contactId, group);
        contactRepository.delete(contact);
    }

    @NotNull
    public ContactGroup getContactGroup(Long groupId) {
        Optional<ContactGroup> groupOptional = contactGroupRepository.findById(groupId);
        return groupOptional.orElseThrow(() -> new DemoException(ErrorCode.CODE_102, ImmutableMap.of(ErrorCode.Constants.PARAMETER_NAME, "groupId")));
    }

    @NotNull
    private Contact getGroupContact(Long contactId, ContactGroup group) {
        Optional<Contact> contactOptional = contactRepository.findFirstByIdAndContactGroup(contactId, group);
        return contactOptional.orElseThrow(() -> new DemoException(ErrorCode.CODE_102, ImmutableMap.of(ErrorCode.Constants.PARAMETER_NAME, "contactId")));
    }
}
