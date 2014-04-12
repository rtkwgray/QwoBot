package com.sl5r0.qwobot.irc.service;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.help.Command;
import com.sl5r0.qwobot.domain.help.CommandDirectory;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

@Singleton
public class HelpService extends AbstractIrcEventService {
    private static final Command helpCommand = new Command("!help", "Search for the specified command and display its usage").addParameter("command");
    private final CommandDirectory commandDirectory;

    @Inject
    public HelpService(CommandDirectory commandDirectory) {
        super(newHashSet(helpCommand));
        this.commandDirectory = checkNotNull(commandDirectory, commandDirectory);
    }

    @Subscribe
    public void help(PrivateMessageEvent<PircBotX> event) {
        findHelp(event, 10);
    }


    @Subscribe
    public void help(MessageEvent<PircBotX> event) {
        findHelp(event, 1);
    }

    private void findHelp(GenericMessageEvent<PircBotX> event, int maxResults) {
        final List<String> arguments = argumentsFor(helpCommand, event.getMessage());
        if (arguments.isEmpty()) {
            event.respond(helpCommand.prettyString());
        } else {
            final List<Command> foundCommands = commandDirectory.search(arguments.get(0), maxResults);
            if (foundCommands.isEmpty()) {
                event.respond("No commands match the specified trigger");
            } else {
                for (Command command : foundCommands) {
                    event.respond(command.prettyString());
                }
            }
        }
    }
}
