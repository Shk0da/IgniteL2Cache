package com.github.shk0da.demo.actor;

import akka.actor.UntypedAbstractActor;
import com.github.shk0da.demo.domain.HistoryAudit;
import com.github.shk0da.demo.repository.HistoryAuditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.sql.SQLException;

import static com.github.shk0da.demo.provider.ApplicationContextProvider.getBean;

@Slf4j
public class AuditActor extends UntypedAbstractActor {

    private HistoryAuditRepository historyAuditRepository;

    @Override
    public void preStart() {
        if (historyAuditRepository == null) {
            historyAuditRepository = getBean(HistoryAuditRepository.class);
        }
    }

    @Override
    @Retryable(value = {SQLException.class}, maxAttempts = 4, backoff = @Backoff(500))
    public void onReceive(Object msg) {
        if (msg instanceof HistoryAudit) {
            HistoryAudit entity = historyAuditRepository.save((HistoryAudit) msg);
            log.debug("HistoryAudit: [{}]", entity);
        } else {
            unhandled(msg);
        }
    }
}
