package com.sl5r0.qwobot.plugins.commands;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.sl5r0.qwobot.plugins.exceptions.CommandExecutionException;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.api.client.repackaged.com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.transform;

public class CompoundCommand extends ParameterizedTriggerCommand {
    private final Map<String, TriggerCommand> commands;

    public CompoundCommand(String trigger, Set<TriggerCommand> commands) {
        super(trigger);
        checkNotNull(commands, "commands cannot be null");
        this.commands = Maps.uniqueIndex(commands, new Function<TriggerCommand, String>() {
            @Override
            public String apply(TriggerCommand input) {
                return input.getTrigger();
            }
        });
    }

    @Override
    public void execute(MessageEvent event, List<String> parameters) {
        if (parameters.isEmpty()) {
            throw new CommandExecutionException("Invalid number of parameters.");
        }

        final String subCommand = parameters.get(0);
        final Optional<Map.Entry<String, TriggerCommand>> command = tryFind(commands.entrySet(), hasTrigger(subCommand));

        if (command.isPresent()) {
            final TriggerCommand innerCommand = command.get().getValue();
            final String messageWithTriggerRemoved = event.getMessage().substring(getTrigger().length()).trim();
            final MessageEvent<PircBotX> newEvent = new MessageEvent<>(
                    event.getBot(), event.getChannel(), event.getUser(), messageWithTriggerRemoved
            );

            if (innerCommand instanceof ParameterizedTriggerCommand) {
                final List<String> newParameters = ImmutableList.copyOf(parameters.subList(1, parameters.size()));
                ((ParameterizedTriggerCommand) innerCommand).execute(newEvent, newParameters);
            } else {
                // It's just a plain old TriggerCommand
                innerCommand.onMessageEvent(newEvent);
            }
        }
    }

    public Map<String, TriggerCommand> getCommands() {
        return copyOf(commands);
    }

    @Override
    public List<String> getHelp() {
        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (TriggerCommand command : commands.values()) {
            final List<String> appendedHelp = transform(command.getHelp(), appendTrigger(getTrigger()));
            builder.addAll(appendedHelp);
        }
        return builder.build();
    }

    private Function<String, String> appendTrigger(final String trigger) {
        return new Function<String, String>() {
            @Override
            public String apply(String helpString) {
                return trigger + " " + helpString;
            }
        };
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
