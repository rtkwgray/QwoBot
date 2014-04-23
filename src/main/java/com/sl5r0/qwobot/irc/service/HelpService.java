package com.sl5r0.qwobot.irc.service;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.command.Command;
import com.sl5r0.qwobot.domain.command.CommandDirectory;
import com.sl5r0.qwobot.domain.command.CommandHandler;
import com.sl5r0.qwobot.domain.command.Parameter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class HelpService extends AbstractIrcEventService {
    private final CommandDirectory commandDirectory;
    private Command<GenericMessageEvent> helpCommand;

    @Inject
    public HelpService(CommandDirectory commandDirectory) {
        this.commandDirectory = checkNotNull(commandDirectory, commandDirectory);
    }

    private void findHelp(GenericMessageEvent event, Optional<String> search, int maxResults) {
        if (search.isPresent()) {
            event.respond(helpCommand.prettyString());
        } else {
            final List<Command> foundCommands = commandDirectory.search(search.get(), maxResults);
            if (foundCommands.isEmpty()) {
                event.respond("No commands match the specified trigger");
            } else {
                for (Command command : foundCommands) {
                    event.respond(command.prettyString());
                }
            }
        }
    }

    @Override
    protected void initialize() {
        // TODO: this should be initialized in the constructor, but I can't do it right now because of how initialize is called.
        helpCommand = Command.forEvent(GenericMessageEvent.class)
                .addParameter(Parameter.exactMatch("!help"))
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
        registerCommand(helpCommand);
    }
}
