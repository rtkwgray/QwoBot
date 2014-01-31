package com.sl5r0.qwobot.plugins.twitter;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.sl5r0.qwobot.plugins.commands.ParameterizedTriggerCommand;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.TwitterException;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class UnfollowUser extends ParameterizedTriggerCommand {
    private static final Logger log = LoggerFactory.getLogger(UnfollowUser.class);
    private static final String TRIGGER = "!unfollow";
    private final TwitterState twitter;

    public UnfollowUser(TwitterState twitterState) {
        super(TRIGGER);
        this.twitter = twitterState;
    }

    @Override
    public void execute(MessageEvent event, List<String> arguments) {
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

        if (!failedUnfollows.isEmpty()) {
            event.getChannel().send().message("Couldn't unfollow: " + newUnfollows);
        }

        final Set<String> follows = twitter.getFollowHandles();
        if (follows.isEmpty()) {
            event.getChannel().send().message("Not following anybody.");
        } else {
            final String following = Joiner.on(", ").skipNulls().join(follows);
            event.getChannel().send().message("Now following: " + following);
        }

        twitter.restartStream();
    }

    @Override
    public String getHelp() {
        return TRIGGER + " <twitter handle> [ <twitter handle> ... ]";
    }
}
