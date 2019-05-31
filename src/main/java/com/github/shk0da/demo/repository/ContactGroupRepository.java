package com.github.shk0da.demo.repository;

import com.github.shk0da.demo.domain.ContactGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactGroupRepository extends JpaRepository<ContactGroup, Long> {
}
