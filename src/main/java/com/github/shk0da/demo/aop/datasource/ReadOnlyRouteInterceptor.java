package com.github.shk0da.demo.aop.datasource;

import com.github.shk0da.demo.persistence.RoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Aspect
@Order(0)
@Component
public class ReadOnlyRouteInterceptor {

    @Around("@annotation(transactional)")
    public Object proceed(ProceedingJoinPoint proceedingJoinPoint, Transactional transactional) throws Throwable {
        try {
            if (transactional.readOnly()) {
                RoutingDataSource.setReplicaRoute();
                log.trace("Routing database call to the read replica");
            }
            return proceedingJoinPoint.proceed();
        } finally {
            RoutingDataSource.clearReplicaRoute();
        }
    }
}