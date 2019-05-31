package com.github.shk0da.demo.aop.audit;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shk0da.demo.config.ActorConfig;
import com.github.shk0da.demo.domain.AuditEntity;
import com.github.shk0da.demo.domain.HistoryAudit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import javax.persistence.PostPersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.util.Date;

import static com.github.shk0da.demo.provider.ApplicationContextProvider.getBean;

@Slf4j
public class EntityAuditListener {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Getter(lazy = true)
    private final ActorSystem auditActorSystem = getBean(ActorConfig.AUDIT_ACTOR_SYSTEM_BEAN, ActorSystem.class);

    /**
     * Insert action
     *
     * @param object {@link @Entity}
     */
    @PostPersist
    public void onPostPersist(Object object) {
        audit(HistoryAudit.Action.INSERT, object);
    }

    /**
     * Update action
     *
     * @param object {@link @Entity}
     */
    @PreUpdate
    public void onPreUpdate(Object object) {
        audit(HistoryAudit.Action.UPDATE, object);
    }

    /**
     * Delete action
     *
     * @param object {@link @Entity}
     */
    @PreRemove
    public void onPreRemove(Object object) {
        audit(HistoryAudit.Action.DELETE, object);
    }

    @Async
    protected void audit(HistoryAudit.Action action, Object entity) {
        Table table = entity.getClass().getAnnotation(Table.class);
        if (table == null) {
            log.error("Failed audit Entity[{}]: @Table not found", entity);
            return;
        }

        String payload;
        try {
            payload = OBJECT_MAPPER.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            log.error("Failed save payload for audit Entity[{}]: {}", entity, e.getMessage());
            payload = "";
        }

        getAuditActorSystem().actorSelection(ActorConfig.AUDIT_ACTOR_PATH_HEAD)
                .tell(HistoryAudit.builder()
                        .action(action)
                        .entityId(((AuditEntity) entity).getId())
                        .tableName(table.name())
                        .timestamp(new Date())
                        .payload(payload)
                        .build(), getAuditActorSystem().guardian());
    }
}
