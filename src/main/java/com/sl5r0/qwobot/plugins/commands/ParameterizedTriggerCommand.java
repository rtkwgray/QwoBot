package com.sl5r0.qwobot.plugins.commands;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.sl5r0.qwobot.plugins.exceptions.CommandExecutionException;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;

/**
 * A command that is executed when a message matches a specific trigger.
 */
public abstract class ParameterizedTriggerCommand extends TriggerCommand implements ParameterizedCommand {
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\"([^\"]*)\"|(^[\"\\S]+)|(\"?\\S+)");
    private final Optional<Function<String, String>> argumentMutator;

    public ParameterizedTriggerCommand(String prefix) {
        this(prefix, null);
    }

    public ParameterizedTriggerCommand(String prefix, Function<String, String> argumentMutator) {
        super(prefix);
        this.argumentMutator = Optional.fromNullable(argumentMutator);
    }

    @Override
    protected final void triggered(MessageEvent event) {
        try {
            if (event.getMessage() == null || event.getMessage().trim().equals(getTrigger())) {
                execute(event, Collections.<String>emptyList());
            } else {
                // Remove the trigger and leading space from the message (it's not a parameter, and can be fetched with
                // getTrigger() if needed).
                final String messageWithoutTrigger = event.getMessage().substring(getTrigger().length() + 1);
                execute(event, parseArguments(messageWithoutTrigger));
            }
        } catch (CommandExecutionException e) {
            event.getChannel().send().message(e.getMessage());
            event.getChannel().send().message("Usage: " + getHelp());
        }
    }

    private List<String> parseArguments(String message) {
        final Matcher matcher = PARAMETER_PATTERN.matcher(message);
        final List<String> parameters = newArrayList();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parameters.add(matcher.group(1));
            } else if (matcher.group(2) != null){
                parameters.add(matcher.group(2));
            } else {
                parameters.add(matcher.group(3));
            }
        }

        if (argumentMutator.isPresent()) {
            return transform(parameters, argumentMutator.get());
        } else {
            return parameters;
        }
    }

    protected static final Function<String, String> TO_LOWERCASE = new Function<String, String>() {
        @Override
        public String apply(String input) {
            return input.toLowerCase();
        }
    };
}
