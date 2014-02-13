package com.sl5r0.qwobot.plugins.twitter;

import com.sl5r0.qwobot.plugins.commands.TriggerCommand;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;

import static java.util.Collections.singletonList;

public class ShowFollows extends TriggerCommand {
    private static final String TRIGGER = "!showfollows";
    private final TwitterState twitterState;

    public ShowFollows(TwitterState twitterState) {
        super(TRIGGER);
        this.twitterState = twitterState;
    }

    @Override
    protected void triggered(MessageEvent event) {
        event.getChannel().send().message("Currently following: " + twitterState.followsToString());
    }

    @Override
    public List<String> getHelp() {
        return singletonList(getTrigger());
    }
}
