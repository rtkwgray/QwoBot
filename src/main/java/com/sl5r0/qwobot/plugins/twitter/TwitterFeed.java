package com.sl5r0.qwobot.plugins.twitter;

import com.sl5r0.qwobot.core.BotConfiguration;
import com.sl5r0.qwobot.core.QwoBot;
import com.sl5r0.qwobot.plugins.Plugin;
import com.sl5r0.qwobot.plugins.commands.Command;
import com.sl5r0.qwobot.plugins.exceptions.PluginInitializationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.pircbotx.Channel;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.NoSuchElementException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

public class TwitterFeed extends Plugin {
    private static final Set<Command> commands = newHashSet();

    public TwitterFeed(BotConfiguration botConfiguration, QwoBot bot) {
        checkNotNull(bot);

        final HierarchicalConfiguration pluginConfig = botConfiguration.configurationAt("plugins.twitter");
        pluginConfig.setThrowExceptionOnMissing(true);

        final Configuration twitterConfiguration;
        final Channel channel;
        try {
            twitterConfiguration = new ConfigurationBuilder()
                    .setDebugEnabled(pluginConfig.getBoolean("debug", false))
                    .setOAuthConsumerKey(pluginConfig.getString("oauth.consumer-key"))
                    .setOAuthConsumerSecret(pluginConfig.getString("oauth.consumer-secret"))
                    .setOAuthAccessToken(pluginConfig.getString("oauth.access-token"))
                    .setOAuthAccessTokenSecret(pluginConfig.getString("oauth.access-token-secret"))
                    .setIncludeRTsEnabled(false)
                    .setUserStreamRepliesAllEnabled(false)
                    .setIncludeMyRetweetEnabled(false)
                    .setUseSSL(true)
                    .build();

            channel = bot.getUserChannelDao().getChannel(pluginConfig.getString("channel"));
        } catch (NoSuchElementException e) {
            throw new PluginInitializationException("Twitter credentials are missing", e);
        }

        final TwitterFactory twitterFactory = new TwitterFactory(twitterConfiguration);
        final TwitterStream twitterStream = new TwitterStreamFactory(twitterConfiguration).getInstance();
        final TwitterState twitterState = new TwitterState(twitterFactory.getInstance(), twitterStream);
        final TwitterListener listener = new TwitterListener(twitterState, channel);
        twitterStream.addListener(listener);

        commands.add(new FollowUser(twitterState));
        commands.add(new UnfollowUser(twitterState));
        commands.add(new ChangeTweetColor(twitterState));
        commands.add(new ToggleReplies(twitterState));
        commands.add(new ToggleRetweets(twitterState));
        commands.add(new ShowFollows(twitterState));
    }

    @Override
    public Set<Command> getCommands() {
        return commands;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getName() {
        return "Twitter";
    }
}
