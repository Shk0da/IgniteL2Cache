package com.github.shk0da.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "history_audit")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class HistoryAudit {

    @Id
    @GeneratedValue
    private Long id;
    private String tableName;
    private Long entityId;
    private Action action;
    private Date timestamp;
    @Column(length = 5_000)
    private String payload;

    public enum Action {INSERT, UPDATE, DELETE}
}
