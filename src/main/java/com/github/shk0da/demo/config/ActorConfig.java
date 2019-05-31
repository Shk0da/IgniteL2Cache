package com.github.shk0da.demo.config;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import com.github.shk0da.demo.actor.AuditActor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ActorConfig {

    public static final String AUDIT_ACTOR_SYSTEM_BEAN = "auditActorSystem";
    private static final String AUDIT_ACTOR_NAME = "AuditActor";
    private static final String AUDIT_ACTOR_SYSTEM_NAME = "audit-system";
    public static final String AUDIT_ACTOR_PATH_HEAD = "akka://" + AUDIT_ACTOR_SYSTEM_NAME + "/user/" + AUDIT_ACTOR_NAME;

    @Bean(name = AUDIT_ACTOR_SYSTEM_BEAN)
    public ActorSystem auditActorSystem() {
        ActorSystem actorSystem = ActorSystem.create(AUDIT_ACTOR_SYSTEM_NAME);
        actorSystem.actorOf(
                Props.create(AuditActor.class).withRouter(new RoundRobinPool(AsyncConfiguration.AVAILABLE_TASK_THREADS)), AUDIT_ACTOR_NAME
        );
        return actorSystem;
    }
}
