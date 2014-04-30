package com.sl5r0.qwobot.irc.service.admin;

import com.google.inject.Inject;
import com.sl5r0.qwobot.irc.command.CommandHandler;
import com.sl5r0.qwobot.irc.IrcServiceManager;
import com.sl5r0.qwobot.irc.service.AbstractIrcEventService;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Service.State.RUNNING;
import static com.sl5r0.qwobot.irc.IrcTextFormatter.GREEN;
import static com.sl5r0.qwobot.irc.IrcTextFormatter.YELLOW;
import static com.sl5r0.qwobot.irc.command.Command.forEvent;
import static com.sl5r0.qwobot.irc.command.Parameter.literal;

public class ManagementService extends AbstractIrcEventService {
    private final IrcServiceManager serviceManager;

    @Inject
    public ManagementService(IrcServiceManager serviceManager) {
        this.serviceManager = checkNotNull(serviceManager, "serviceManager must not be null");
    }

    public void listServices(PrivateMessageEvent event) {
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

    @Override
    protected void initialize() {
        registerCommand(forEvent(PrivateMessageEvent.class)
                .addParameters(literal("!services:list"))
                .description("Display bot service status")
                .handler(new CommandHandler<PrivateMessageEvent>() {
                    @Override
                    public void handle(PrivateMessageEvent event, List<String> arguments) {
                        listServices(event);
                    }
                })
                .build());
    }
}
