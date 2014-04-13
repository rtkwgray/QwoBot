package com.sl5r0.qwobot.core;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.security.IrcAuthenticationToken;
import com.sl5r0.qwobot.security.exceptions.BotCannotSeeUserException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.types.GenericUserEvent;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class IrcEventDispatcher extends ListenerAdapter<PircBotX> {
    private static final Logger log = getLogger(IrcEventDispatcher.class);
    private final ExecutorService executor = newFixedThreadPool(50);
    private final EventBus eventBus;

    @Inject
    public IrcEventDispatcher(EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus, "eventBus cannot be null");
    }

    @Override
    public void onEvent(final Event<PircBotX> event) throws Exception {
        super.onEvent(event);
        // Rely on our own executor to deal with events. This means that commands will be processed synchronously, but
        // it's easier to maintain authentication state this way.
        executor.submit(new Runnable() {
            @Override
            public void run() {
                // Don't try to authenticate during JoinEvents because we might not be logged in yet.
                if (event instanceof GenericUserEvent && !(event instanceof JoinEvent)) {
                    final User user = ((GenericUserEvent) event).getUser();
                    if (user.equals(user.getBot().getUserBot())) {
                        log.trace("Skipping authentication for GenericUserEvent from bot");
                    } else {
                        logInSubject(user);
                    }
                }

                eventBus.post(event);
            }
        });
    }

    private void logInSubject(User user) {
        final Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(new IrcAuthenticationToken(user));
        } catch (BotCannotSeeUserException e) {
            log.trace("Ignoring failed authentication for nick \"" + user.getNick() + "\" because I can't see them.");
        } catch (UnknownAccountException e) {
            log.trace("Ignoring unknown account " + user.getNick() + ". They need to log in first.");
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user with nickname \"" + user.getNick() + "\"", e);
        }
    }
}
