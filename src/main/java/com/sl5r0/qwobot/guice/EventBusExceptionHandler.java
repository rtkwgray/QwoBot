package com.sl5r0.qwobot.guice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.shiro.authz.UnauthorizedException;
import org.pircbotx.hooks.types.GenericEvent;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class EventBusExceptionHandler implements MethodInterceptor {
    private static final Logger log = getLogger(EventBusExceptionHandler.class);

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            return methodInvocation.proceed();
        } catch (UnauthorizedException authorizationException) {
            if (methodInvocation.getArguments().length == 1) {
                final Object argument = methodInvocation.getArguments()[0];
                if (argument instanceof GenericEvent) {
                    ((GenericEvent) argument).respond("Can't let you do that, Star Fox.");
                }
            }
        } catch (Throwable throwable) {
            log.error("Exception caught during " + methodInvocation.getMethod().toGenericString(), throwable);
        }

        return null; // OK to return null here because subscribe methods are all void.
    }
}
