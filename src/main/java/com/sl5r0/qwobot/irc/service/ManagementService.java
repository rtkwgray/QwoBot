package com.sl5r0.qwobot.irc.service;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.sl5r0.qwobot.domain.help.Command;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.util.concurrent.Service.State.RUNNING;
import static com.sl5r0.qwobot.core.IrcTextFormatter.GREEN;
import static com.sl5r0.qwobot.core.IrcTextFormatter.YELLOW;

public class ManagementService extends AbstractIrcEventService {
    private static final Command listServices = new Command("!services:list", "List the status of all bot services");
    private final IrcServiceManager serviceManager;

    @Inject
    public ManagementService(IrcServiceManager serviceManager) {
        super(newHashSet(listServices));
        this.serviceManager = checkNotNull(serviceManager, "serviceManager must not be null");
    }

    @Subscribe
    public void listServices(PrivateMessageEvent<PircBotX> event) {
        argumentsFor(listServices, event.getMessage());
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
