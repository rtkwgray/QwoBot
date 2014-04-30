package com.sl5r0.qwobot.irc.service.help;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.irc.command.Command;
import com.sl5r0.qwobot.irc.command.CommandDirectory;
import com.sl5r0.qwobot.irc.command.CommandHandler;
import com.sl5r0.qwobot.irc.service.AbstractIrcEventService;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sl5r0.qwobot.irc.command.Parameter.literal;
import static com.sl5r0.qwobot.irc.command.Parameter.optional;
import static com.sl5r0.qwobot.irc.command.Parameter.string;

@Singleton
public class HelpService extends AbstractIrcEventService {
    private final CommandDirectory commandDirectory;
    private final Command<GenericMessageEvent> helpCommand = Command.forEvent(GenericMessageEvent.class)
            .addParameters(literal("!help"), optional(string("command name")))
            .description("Display usage for the specified command")
            .handler(new CommandHandler<GenericMessageEvent>() {
                @Override
                public void handle(GenericMessageEvent event, List<String> arguments) {
                    if (arguments.size() > 1) {
                        findHelp(event, Optional.of(arguments.get(1)), 5);
                    } else {
                        findHelp(event, Optional.<String>absent(), 5);
                    }
                }
            })
            .build();

    @Inject
    public HelpService(CommandDirectory commandDirectory) {
        this.commandDirectory = checkNotNull(commandDirectory, commandDirectory);
    }

    private void findHelp(GenericMessageEvent event, Optional<String> search, int maxResults) {
        if (!search.isPresent()) {
            event.respond(helpCommand.usageString());
        } else {
            final List<Command> foundCommands = commandDirectory.search(search.get(), maxResults);
            if (foundCommands.isEmpty()) {
                event.respond("No commands match the specified trigger");
            } else {
                for (Command command : foundCommands) {
                    event.respond(command.usageString());
                }
            }
        }
    }

    @Override
    protected void initialize() {
        registerCommand(helpCommand);
    }
}
