package com.sl5r0.qwobot.plugins.twitter;

import com.sl5r0.qwobot.plugins.commands.ToggleCommand;
import org.pircbotx.hooks.events.MessageEvent;

public class ToggleReplies extends ToggleCommand {
    private static final String TRIGGER = "!replies";
    private final TwitterState twitterState;

    public ToggleReplies(TwitterState twitterState) {
        super(TRIGGER);
        this.twitterState = twitterState;
    }

    @Override
    protected void execute(MessageEvent event, boolean repliesEnabled) {
        twitterState.setShowingReplies(repliesEnabled);
        if (repliesEnabled) {
            event.getChannel().send().message("Twitter replies are now enabled.");
        } else {
            event.getChannel().send().message("Twitter replies are now disabled.");
        }
    }
}
