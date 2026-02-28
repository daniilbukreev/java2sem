package com.mipt.daniilbukreev.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for logging execution of service methods
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    /**
     * Pointcut that matches all methods in the service package.
     */
    @Pointcut("within(com.mipt.daniilbukreev.service..*)")
    public void serviceMethods() {}

    /**
     * Advice that logs around the execution of methods matched by the serviceMethods pointcut.
     * @param joinPoint the join point for the advised method.
     * @return the result of the method execution.
     * @throws Throwable if the advised method throws an exception.
     */
    @Around("serviceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        logger.info("==> Entering method: {} with arguments: {}", methodName, Arrays.toString(args));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error("<== Error in method: {} - Exception: {}", methodName, throwable.getMessage());
            throw throwable;
        }

        if (joinPoint.getSignature().toShortString().contains("void")) {
            logger.info("<== Exiting method: {} (void)", methodName);
        } else {
            logger.info("<== Exiting method: {} with result: {}", methodName, result);
        }

        return result;
    }
}
