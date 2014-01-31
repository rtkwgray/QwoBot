package com.sl5r0.qwobot.plugins.twitter;

import com.sl5r0.qwobot.plugins.commands.ToggleCommand;
import org.pircbotx.hooks.events.MessageEvent;

public class ToggleRetweets extends ToggleCommand {
    private static final String TRIGGER = "!retweets";
    private final TwitterState twitterState;

    public ToggleRetweets(TwitterState twitterState) {
        super(TRIGGER);
        this.twitterState = twitterState;
    }

    @Override
    protected void execute(MessageEvent event, boolean retweetsEnabled) {
        twitterState.setShowingRetweets(retweetsEnabled);
        if (retweetsEnabled) {
            event.getChannel().send().message("Retweets are now enabled.");
        } else {
            event.getChannel().send().message("Retweets are now disabled.");
        }
    }
}
