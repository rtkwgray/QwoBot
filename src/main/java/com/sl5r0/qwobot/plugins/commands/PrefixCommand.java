package com.sl5r0.qwobot.plugins.commands;

import com.google.common.eventbus.Subscribe;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

/**
 * A command that is executed when a message matches a specific prefix.
 */
public abstract class PrefixCommand extends MessageCommand {
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\"([^\"]*)\"|(^[\"\\S]+)|(\"?\\S+)");
    private final String prefix;

    public PrefixCommand(String prefix) {
        this.prefix = checkNotNull(prefix);
    }

    @Subscribe
    public final void execute(MessageEvent event) {
        if (event.getMessage().startsWith(prefix)) {
            execute(event, parseArguments(event.getMessage()));
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
        parameters.remove(prefix);
        return parameters;
    }
}
