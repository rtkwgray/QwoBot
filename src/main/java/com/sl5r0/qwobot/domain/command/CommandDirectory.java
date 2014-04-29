package com.sl5r0.qwobot.domain.command;

import com.google.common.base.Predicate;
import com.google.inject.Singleton;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Sets.newHashSet;
import static org.pircbotx.Colors.removeFormattingAndColors;

@Singleton
public class CommandDirectory {
    private final Set<Command> commands = newHashSet();

    public void register(Set<Command> commands) {
        checkNotNull(commands, "command must not be null");
        this.commands.addAll(commands);
    }

    public void unregister(Set<Command> commands) {
        this.commands.removeAll(commands);
    }

    public List<Command> search(final String commandString, int maxResults) {
        final Iterable<Command> matchingCommands = filter(commands, new Predicate<Command>() {
            @Override
            public boolean apply(Command input) {
                return removeFormattingAndColors(input.usageString()).contains(commandString);
            }
        });

        return Command.usageStringOrdering.leastOf(matchingCommands, maxResults);
    }
}
