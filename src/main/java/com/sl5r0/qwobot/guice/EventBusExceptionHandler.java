package com.sl5r0.qwobot.guice;

import com.sl5r0.qwobot.irc.service.exceptions.CommandNotApplicableException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.pircbotx.hooks.types.GenericEvent;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class EventBusExceptionHandler implements MethodInterceptor {
    private static final Logger log = getLogger(EventBusExceptionHandler.class);

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            return methodInvocation.proceed();
        } catch (UnauthorizedException | AuthenticationException | UnauthenticatedException e) {
            if (methodInvocation.getArguments().length == 1) {
                final Object argument = methodInvocation.getArguments()[0];
                if (argument instanceof GenericEvent) {
                    if (e instanceof UnauthorizedException) {
                        ((GenericEvent) argument).respond("Sorry, but it doesn't look like you're authorized to do that.");
                    } else {
                        ((GenericEvent) argument).respond("You need to log in before you can do that.");
                    }
                }
            }
        } catch (CommandNotApplicableException e) {
            log.trace("Command not applicable to message: " + e.getMessage());
        } catch (Throwable throwable) {
            log.error("Exception caught during " + methodInvocation.getMethod().toGenericString(), throwable);
        }

        return null; // OK to return null here because subscribe methods are all void.
    }
}
