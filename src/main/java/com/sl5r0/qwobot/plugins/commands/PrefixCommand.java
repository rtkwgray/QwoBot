package com.sl5r0.qwobot.plugins.commands;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.sl5r0.qwobot.plugins.exceptions.CommandExecutionException;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;

/**
 * A command that is executed when a message matches a specific prefix.
 */
public abstract class PrefixCommand extends MessageCommand {
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\"([^\"]*)\"|(^[\"\\S]+)|(\"?\\S+)");
    private final String prefix;
    private final Optional<Function<String, String>> argumentMutator;

    public PrefixCommand(String prefix) {
        this(prefix, null);
    }

    public PrefixCommand(String prefix, Function<String, String> argumentMutator) {
        this.prefix = checkNotNull(prefix);
        this.argumentMutator = Optional.fromNullable(argumentMutator);
    }

    @Subscribe
    public final void execute(MessageEvent event) {
        if (event.getMessage().startsWith(prefix)) {
            try {
                execute(event, parseArguments(event.getMessage()));
            } catch (CommandExecutionException e) {
                event.getChannel().sendMessage(e.getMessage());
                event.getChannel().sendMessage("Usage: " + getHelp());
            }
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
        parameters.remove(0);
        if (argumentMutator.isPresent()) {
            return transform(parameters, argumentMutator.get());
        } else {
            return parameters;
        }
    }

    public String getPrefix() {
        return prefix;
    }

    protected static final Function<String, String> TO_LOWERCASE = new Function<String, String>() {
        @Override
        public String apply(String input) {
            return input.toLowerCase();
        }
    };
}
