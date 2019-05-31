package com.github.shk0da.demo.service;

import com.github.shk0da.demo.domain.Contact;
import com.github.shk0da.demo.model.Paging;
import com.github.shk0da.demo.model.contacts.ContactListModel;
import com.github.shk0da.demo.model.contacts.ContactModel;
import com.github.shk0da.demo.model.contacts.ContactsSearch;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.joining;

@Slf4j
@Service
@AllArgsConstructor
public class ContactsService {

    private final EntityManager entityManager;

    private final String allFieldsForSort = String.join(",",
            "first_name",
            "last_name"
    );
    private final String allFieldsForSearch = String.join(",",
            "first_name",
            "last_name"
    );

    /**
     * Search contacts
     *
     * @param search   {@link ContactsSearch}
     * @param pageable {@link Pageable}
     * @return {@link ContactListModel}
     */
    public ContactListModel search(ContactsSearch search, Pageable pageable) {
        List<Long> groupIdsForSearch = newArrayList(-1L);
        String searchString = search.getSearchString() != null ? search.getSearchString() : "";

        String orderBy = pageable.getSort() != null ? newArrayList(pageable.getSort().iterator())
                .stream()
                .map(order -> UPPER_CAMEL.to(UPPER_UNDERSCORE, order.getProperty()) + " " + order.getDirection().name())
                .collect(joining(", ")) : allFieldsForSort;
        String sqlSearch = "select * from contact c where c.contact_group_id in (:groupIds) and concat(" + allFieldsForSearch + ") like :search order by " + orderBy;
        @SuppressWarnings("unchecked")
        List<Contact> contacts = entityManager.createNativeQuery(sqlSearch, Contact.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .setParameter("groupIds", groupIdsForSearch)
                .setParameter("search", "%" + searchString + "%")
                .getResultList();
        List<ContactModel> records = contacts.stream().map(ContactModel::of).collect(Collectors.toList());

        String sqlCountQuery = "select count(*) from (" + sqlSearch + ") as slct";
        BigInteger totalRecords = (BigInteger) entityManager.createNativeQuery(sqlCountQuery)
                .setParameter("groupIds", groupIdsForSearch)
                .setParameter("search", "%" + searchString + "%")
                .getSingleResult();
        return new ContactListModel(records, new Paging(totalRecords.intValue(), pageable.getPageNumber() + 1, pageable.getPageSize()));
    }
}
