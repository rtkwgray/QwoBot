package com.sl5r0.qwobot.core;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.security.IrcAuthenticationToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericUserEvent;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class IrcEventDispatcher extends ListenerAdapter<PircBotX> {
    private static final Logger log = getLogger(IrcEventDispatcher.class);
    private final EventBus eventBus;

    @Inject
    public IrcEventDispatcher(EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus, "eventBus cannot be null");
    }

    @Override
    public void onEvent(Event<PircBotX> event) throws Exception {
        log.trace("Received IRC event: " + event);

        if (event instanceof GenericUserEvent) {
            final Subject subject = SecurityUtils.getSubject();
            subject.login(new IrcAuthenticationToken(((GenericUserEvent) event).getUser().getNick()));
        }

        super.onEvent(event);
        eventBus.post(event);
    }
}
