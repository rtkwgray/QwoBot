package com.sl5r0.qwobot.core;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

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
        super.onEvent(event);
        eventBus.post(event);
    }
}
