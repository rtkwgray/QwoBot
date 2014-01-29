package com.sl5r0.qwobot.plugins.twitter;

import com.sl5r0.qwobot.plugins.commands.TriggerCommand;
import org.pircbotx.hooks.events.MessageEvent;

public class ShowFollows extends TriggerCommand {
    private static final String TRIGGER = "!showfollows";
    private final TwitterState twitterState;

    public ShowFollows(TwitterState twitterState) {
        super(TRIGGER);
        this.twitterState = twitterState;
    }

    @Override
    protected void triggered(MessageEvent event) {
        event.getChannel().sendMessage("Currently following: " + twitterState.followsToString());
    }

    @Override
    public String getHelp() {
        return getTrigger();
    }
}
