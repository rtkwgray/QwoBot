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

public class FollowUser extends ParameterizedTriggerCommand {
    private static final Logger log = LoggerFactory.getLogger(FollowUser.class);
    private static final String TRIGGER = "!follow";
    private final TwitterState twitter;

    public FollowUser(TwitterState twitterState) {
        super(TRIGGER);
        this.twitter = twitterState;
    }

    @Override
    public void execute(MessageEvent event, List<String> arguments) {
        final Set<String> newFollows = newHashSet();
        final Set<String> failedFollows = newHashSet();
        for (String twitterHandle : arguments) {
            try {
                twitter.follow(twitterHandle);
                newFollows.add(twitterHandle);
                log.info("Now following " + newFollows);
            } catch (TwitterException e) {
                failedFollows.add(twitterHandle);
                log.warn("Couldn't follow " + twitterHandle, e);
            }
        }

        if (!failedFollows.isEmpty()) {
            event.getChannel().sendMessage("Couldn't follow: " + newFollows);
        }

        final Set<String> follows = twitter.getFollowHandles();
        if (follows.isEmpty()) {
            event.getChannel().sendMessage("Not following anybody.");
        } else {
            final String following = Joiner.on(", ").skipNulls().join(follows);
            event.getChannel().sendMessage("Now following: " + following);
        }

        twitter.restartStream();
    }

    @Override
    public String getHelp() {
        return TRIGGER + " <twitter handle> [ <twitter handle> ... ]";
    }
}
