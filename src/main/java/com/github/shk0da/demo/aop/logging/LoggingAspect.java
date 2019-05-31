package com.github.shk0da.demo.aop.logging;

import com.github.shk0da.demo.config.ProfileConfigConstants;
import com.github.shk0da.demo.exception.DemoException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Slf4j
@Aspect
@AllArgsConstructor
public class LoggingAspect {

    private final Environment env;

    @SuppressWarnings("unused")
    @Pointcut("within(com.github.shk0da.demo.repository..*) || within(com.github.shk0da.demo.service..*) || within(com.github.shk0da.demo.controller..*)")
    public void loggingPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice
     * @param e         exception
     */
    @AfterThrowing(pointcut = "loggingPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        String cause = (e instanceof DemoException)
                ? ((DemoException) e).getError()
                : ExceptionUtils.getRootCause(e.getCause()).getMessage();
        if (env.acceptsProfiles(ProfileConfigConstants.SPRING_PROFILE_DEVELOPMENT)) {
            log.error("Exception in {}.{}() with cause = \'{}\' and exception = \'{}\'",
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), cause, e);
        } else {
            log.error("Exception in {}.{}() with cause = {}",
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), cause);
        }
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */
    @Around("loggingPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (log.isDebugEnabled()) {
                log.debug("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(), result);
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }
}
