package com.sl5r0.qwobot.plugins.twitter;

import com.sl5r0.qwobot.plugins.commands.PrefixCommand;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.TwitterException;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class FollowUser extends PrefixCommand {
    private static final Logger log = LoggerFactory.getLogger(FollowUser.class);
    private static final String TRIGGER = "!follow";
    private final TwitterState twitter;
    private final Channel channel;

    public FollowUser(TwitterState twitterState, Channel channel) {
        super(TRIGGER);
        this.twitter = twitterState;
        this.channel = channel;
    }

    @Override
    protected void execute(MessageEvent event, List<String> arguments) {
        final Set<String> newFollows = newHashSet();
        final Set<String> failedFollows = newHashSet();
        for (String twitterHandle : arguments) {
            try {
                twitter.follow(twitterHandle);
                newFollows.add(twitterHandle);
            } catch (TwitterException e) {
                log.warn("Couldn't follow " + twitterHandle, e);
                failedFollows.add(twitterHandle);
            }
        }

        if (!newFollows.isEmpty()) {
            channel.sendMessage("Now following: " + newFollows);
        }

        if (!failedFollows.isEmpty()) {
            channel.sendMessage("Couldn't follow: " + newFollows);
        }
    }

    @Override
    public String getHelp() {
        return TRIGGER + " <twitter handle> [ <twitter handle> ... ]";
    }
}