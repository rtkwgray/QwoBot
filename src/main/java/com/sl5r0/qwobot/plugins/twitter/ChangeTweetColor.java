package com.sl5r0.qwobot.plugins.twitter;

import com.sl5r0.qwobot.core.QwoBot;
import com.sl5r0.qwobot.plugins.commands.PrefixCommand;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChangeTweetColor extends PrefixCommand {
    private static final String TRIGGER = "!tweetcolor";
    private final TwitterListener twitterListener;

    public ChangeTweetColor(TwitterListener twitterListener) {
        super(TRIGGER);
        this.twitterListener = checkNotNull(twitterListener);
    }

    @Override
    protected void execute(MessageEvent event, List<String> arguments) {
        final Map<String, String> colorMap = QwoBot.IRC_COLORS;
        final String newColor = arguments.get(0);
        if (colorMap.containsKey(newColor)) {
            twitterListener.setTweetColor(colorMap.get(newColor));
        }
    }

    @Override
    public String getHelp() {
        return TRIGGER + " " + QwoBot.IRC_COLORS.keySet();
    }
}
