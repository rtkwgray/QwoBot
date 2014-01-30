package com.sl5r0.qwobot.plugins.commands;

import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.api.client.util.Lists.newArrayList;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class RegexCommand extends MessageCommand implements ParameterizedCommand {
    private final Pattern pattern;

    public RegexCommand(Pattern pattern) {
        this.pattern = checkNotNull(pattern);
    }

    @Override
    public final void onMessageEvent(MessageEvent event) {
        final Matcher matcher = pattern.matcher(event.getMessage());
        final List<String> matches = newArrayList();
        while(matcher.find()) {
            matches.add(matcher.group());
        }

        if (!matches.isEmpty()) {
            execute(event, matches);
        }
    }

    @Override
    public String getHelp() {
        return "regex(" + pattern + ")";
    }
}
