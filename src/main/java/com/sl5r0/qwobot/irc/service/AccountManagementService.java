package com.sl5r0.qwobot.irc.service;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractService;
import com.sl5r0.qwobot.core.QwoBot;

import static com.google.common.base.Preconditions.checkNotNull;

public class AccountManagementService extends AbstractService {
    private final QwoBot bot;
    private final EventBus eventBus;

    public AccountManagementService(QwoBot bot, EventBus eventBus) {
        this.bot = checkNotNull(bot, "bot must not be null");
        this.eventBus = checkNotNull(eventBus, "eventBus must not be null");
    }

    @Override
    protected void doStart() {
        eventBus.register(this);
        notifyStarted();
    }

    @Override
    protected void doStop() {
        eventBus.unregister(this);
        notifyStopped();
    }
}
