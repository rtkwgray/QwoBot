package com.sl5r0.qwobot.plugins.twitter;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.sl5r0.qwobot.core.Format;
import com.sl5r0.qwobot.plugins.commands.ParameterizedTriggerCommand;
import com.sl5r0.qwobot.plugins.exceptions.CommandExecutionException;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChangeTweetColor extends ParameterizedTriggerCommand {
    private static final String TRIGGER = "!tweetcolor";
    private final TwitterState twitterState;

    public ChangeTweetColor(TwitterState twitterState) {
        super(TRIGGER);
        this.twitterState = checkNotNull(twitterState);
    }

    @Override
    public void execute(MessageEvent event, List<String> arguments) {
        final Format newColor;
        try {
            newColor = Format.fromString(arguments.get(0));
        } catch (IllegalArgumentException e) {
            throw new CommandExecutionException("I don't understand the color \"" + arguments.get(0) + "\"");
        }
        twitterState.setTweetColor(newColor);
    }

    @Override
    public String getHelp() {
        return TRIGGER + " <" + Joiner.on("|").join(Format.values()) + ">";
    }
}
