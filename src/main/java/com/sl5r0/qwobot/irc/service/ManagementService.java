package com.sl5r0.qwobot.irc.service;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.sl5r0.qwobot.irc.service.runnables.MessageRunnable;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Service.State.RUNNING;
import static com.sl5r0.qwobot.core.IrcTextFormatter.GREEN;
import static com.sl5r0.qwobot.core.IrcTextFormatter.YELLOW;
import static com.sl5r0.qwobot.irc.service.MessageDispatcher.startingWithTrigger;

public class ManagementService extends AbstractIdleService {
    private final EventBus eventBus;
    private final MessageDispatcher messageDispatcher;
    private final IrcServiceManager serviceManager;

    @Inject
    public ManagementService(EventBus eventBus, MessageDispatcher messageDispatcher, IrcServiceManager serviceManager) {
        this.eventBus = checkNotNull(eventBus, "eventBus must not be null");
        this.messageDispatcher = checkNotNull(messageDispatcher, "messageDispatcher must not be null");
        this.serviceManager = checkNotNull(serviceManager, "serviceManager must not be null");
        this.messageDispatcher.subscribeToPrivateMessage(startingWithTrigger("'services"), new CheckServiceState());
    }

    @Override
    protected void startUp() throws Exception {
        eventBus.register(messageDispatcher);
    }

    @Override
    protected void shutDown() throws Exception {
        eventBus.unregister(messageDispatcher);
    }

    private class CheckServiceState implements MessageRunnable {
        @Override
        public void run(GenericMessageEvent<PircBotX> event, List<String> arguments) {
            final Map<String, State> services = serviceManager.getServices();
            event.respond("QwoBot services:");
            for (Map.Entry<String, State> serviceState : services.entrySet()) {
                if (serviceState.getValue() == RUNNING) {
                    event.respond(GREEN.format(serviceState.getValue().toString()) + ": " + serviceState.getKey());
                } else {
                    event.respond(YELLOW.format(serviceState.getValue().toString()) + ": " + serviceState.getKey());
                }
            }
        }
    }
}
