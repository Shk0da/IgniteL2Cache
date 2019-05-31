package com.github.shk0da.demo.repository;

import com.github.shk0da.demo.domain.HistoryAudit;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface HistoryAuditRepository extends PagingAndSortingRepository<HistoryAudit, Long> {

    @Transactional(readOnly = true)
    List<HistoryAudit> findAllByTableNameAndTimestampAfter(String tableName, Date timestamp);
}
