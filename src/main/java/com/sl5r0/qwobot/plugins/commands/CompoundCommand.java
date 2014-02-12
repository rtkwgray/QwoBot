package com.sl5r0.qwobot.plugins.commands;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.sl5r0.qwobot.plugins.exceptions.CommandExecutionException;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;
import java.util.Map;

import static com.google.api.client.repackaged.com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Iterables.tryFind;

public class CompoundCommand extends ParameterizedTriggerCommand {
    private final Map<String, TriggerCommand> commands;

    public CompoundCommand(String trigger, Map<String, TriggerCommand> commands) {
        super(trigger);
        this.commands = copyOf(checkNotNull(commands, "commands cannot be null"));
    }

    @Override
    public void execute(MessageEvent event, List<String> parameters) {
        if (parameters.size() < 1) {
            throw new CommandExecutionException("Invalid number of parameters.");
        }

        final String subCommand = parameters.get(0);
        final Optional<Map.Entry<String, TriggerCommand>> command = tryFind(commands.entrySet(), hasTrigger(subCommand));

        if (command.isPresent()) {
            final TriggerCommand innerCommand = command.get().getValue();
            if (innerCommand instanceof ParameterizedTriggerCommand) {
                final List<String> newParameters = ImmutableList.copyOf(parameters.subList(1, parameters.size()));
                ((ParameterizedTriggerCommand) innerCommand).execute(event, newParameters);
            } else {
                // It's just a plain old TriggerCommand
                innerCommand.onMessageEvent(event);
            }
        }
    }

    private static Predicate<Map.Entry<String, TriggerCommand>> hasTrigger(final String trigger) {
        return new Predicate<Map.Entry<String, TriggerCommand>>() {
            @Override
            public boolean apply(Map.Entry<String, TriggerCommand> input) {
                return input.getKey().equals(trigger);
            }
        };
    }
}
