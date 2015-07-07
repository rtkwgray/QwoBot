package com.sl5r0.qwobot.guice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class EventBusExceptionHandler implements MethodInterceptor {
    private static final Logger log = getLogger(EventBusExceptionHandler.class);

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            return methodInvocation.proceed();
        } catch (RuntimeException e) {
            log.error("Exception caught during " + methodInvocation.getMethod().toGenericString(), e);
        }

        return null; // OK to return null here because subscribe methods are all void.
    }
}
