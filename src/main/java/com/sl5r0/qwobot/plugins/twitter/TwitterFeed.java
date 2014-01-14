package com.sl5r0.qwobot.plugins.twitter;

import com.sl5r0.qwobot.plugins.Plugin;
import com.sl5r0.qwobot.plugins.commands.Command;
import org.pircbotx.Channel;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class TwitterFeed extends Plugin {
    private static final Set<Command> commands = newHashSet();

    public TwitterFeed(Channel channel) {
        final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(new TwitterListener(channel));

        final TwitterState twitterState = new TwitterState(TwitterFactory.getSingleton(), twitterStream);
        commands.add(new FollowUser(twitterState, channel));
        commands.add(new UnfollowUser(twitterState, channel));
    }

    @Override
    public Set<Command> getCommands() {
        return commands;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
