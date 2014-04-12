package com.sl5r0.qwobot.domain.help;

import com.google.common.base.Predicate;
import com.google.inject.Singleton;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Sets.newHashSet;
import static com.sl5r0.qwobot.domain.help.Command.triggerOrdering;

@Singleton
public class CommandDirectory {
    private final Set<Command> commands = newHashSet();

    public void register(Command command) {
        checkNotNull(command, "command must not be null");
        checkState(!commands.contains(command), "command " + command + " already registered");
        commands.add(command);
    }

    public List<Command> search(final String commandString, int maxResults) {
        final Iterable<Command> matchingCommands = filter(commands, new Predicate<Command>() {
            @Override
            public boolean apply(Command input) {
                return input.trigger().startsWith(commandString);
            }
        });

        return triggerOrdering.leastOf(matchingCommands, maxResults);
    }
}
