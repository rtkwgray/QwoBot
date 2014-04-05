package com.sl5r0.qwobot.irc.service;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractService;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class AbstractIrcEventService extends AbstractService {
    private final EventBus eventBus;
    protected final Logger log = getLogger(getClass());

    protected AbstractIrcEventService(EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus, "eventBus cannot be null");
    }

    @Override
    protected void doStart() {
        log.info("Starting service.");
        eventBus.register(this);
        notifyStarted();
    }

    @Override
    protected void doStop() {
        log.info("Stopping service.");
        eventBus.unregister(this);
        notifyStarted();
    }
}
