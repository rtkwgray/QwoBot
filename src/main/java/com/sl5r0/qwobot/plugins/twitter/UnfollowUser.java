package com.sl5r0.qwobot.plugins.twitter;

import com.sl5r0.qwobot.plugins.commands.PrefixCommand;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.TwitterException;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class UnfollowUser extends PrefixCommand {
    private static final Logger log = LoggerFactory.getLogger(UnfollowUser.class);
    private static final String TRIGGER = "!unfollow";
    private final TwitterState twitter;

    public UnfollowUser(TwitterState twitterState) {
        super(TRIGGER);
        this.twitter = twitterState;
    }

    @Override
    protected void execute(MessageEvent event, List<String> arguments) {
        final Set<String> newUnfollows = newHashSet();
        final Set<String> failedUnfollows = newHashSet();
        for (String twitterHandle : arguments) {
            try {
                twitter.unfollow(twitterHandle);
                newUnfollows.add(twitterHandle);
            } catch (TwitterException e) {
                log.warn("Couldn't unfollow " + twitterHandle, e);
                failedUnfollows.add(twitterHandle);
            }
        }

        if (!newUnfollows.isEmpty()) {
            event.getChannel().sendMessage("Now unfollowing: " + newUnfollows);
        }

        if (!failedUnfollows.isEmpty()) {
            event.getChannel().sendMessage("Couldn't unfollow: " + newUnfollows);
        }
    }

    @Override
    public String getHelp() {
        return TRIGGER + " <twitter handle> [ <twitter handle> ... ]";
    }
}
