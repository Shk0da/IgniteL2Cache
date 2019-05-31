package com.github.shk0da.demo.repository;

import com.github.shk0da.demo.domain.Contact;
import com.github.shk0da.demo.domain.ContactGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ContactRepository extends PagingAndSortingRepository<Contact, Long> {

    @Transactional(readOnly = true)
    Contact findFirstByIdAndVersion(Long id, Long version);

    @Transactional(readOnly = true)
    Optional<Contact> findFirstByIdAndContactGroup(Long id, ContactGroup contactGroup);

    @Transactional(readOnly = true)
    @Query("select c from Contact c where c.contactGroup = :group and (upper(c.firstName) like concat(upper(:search), '%') or upper(c.lastName) like concat(upper(:search), '%'))")
    Page<Contact> findAllByContactGroupAndSearch(@Param("group") ContactGroup group, @Param("search") String search, Pageable pageable);

    @Transactional(readOnly = true)
    @Query("select c from Contact c where c.id in :ids and c.contactGroup = :group and (upper(c.firstName) like concat(upper(:search), '%') or upper(c.lastName) like concat(upper(:search), '%'))")
    Page<Contact> findAllByContactGroupAndSearchAndIds(@Param("group") ContactGroup group, @Param("search") String search, @Param("ids") List<Long> ids, Pageable pageable);

    @Transactional(readOnly = true)
    List<Contact> findAllByContactGroup(ContactGroup contactGroup);

    @Transactional(readOnly = true)
    Page<Contact> findAllByContactGroup(ContactGroup contactGroup, Pageable pageable);

    int deleteAllByContactGroup(ContactGroup contactGroup);
}
