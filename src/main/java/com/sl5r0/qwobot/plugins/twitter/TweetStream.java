package com.sl5r0.qwobot.plugins.twitter;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sl5r0.qwobot.core.QwoBot;
import com.sl5r0.qwobot.plugins.ConfigurablePlugin;
import com.sl5r0.qwobot.plugins.commands.Command;
import org.pircbotx.Channel;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

public class TweetStream extends ConfigurablePlugin {
    private static final String OAUTH_CONSUMER_KEY = "oauth.consumer-key";
    private static final String OAUTH_CONSUMER_SECRET = "oauth.consumer-secret";
    private static final String OAUTH_ACCESS_TOKEN = "oauth.access-token";
    private static final String OAUTH_ACCESS_TOKEN_SECRET = "oauth.access-token-secret";
    private static final String CHANNEL = "channel";
    private final Provider<QwoBot> botProvider;

    @Inject
    public TweetStream(Provider<QwoBot> botProvider) {
        this.botProvider = checkNotNull(botProvider, "botProvider must not be null");
    }

    @Override
    public Set<Command> getCommands() {
        final Configuration twitterConfiguration = createTwitterConfiguration();
        final TwitterFactory twitterFactory = new TwitterFactory(twitterConfiguration);
        final TwitterStream twitterStream = new TwitterStreamFactory(twitterConfiguration).getInstance();
        final TwitterState twitterState = new TwitterState(twitterFactory.getInstance(), twitterStream);
        final Channel channel = botProvider.get().getUserChannelDao().getChannel(config.getString(CHANNEL));
        final TwitterListener listener = new TwitterListener(twitterState, channel);

        twitterStream.addListener(listener);

        return ImmutableSet.<Command>builder()
                .add(new FollowUser(twitterState))
                .add(new UnfollowUser(twitterState))
                .add(new ChangeTweetColor(twitterState))
                .add(new ToggleReplies(twitterState))
                .add(new ToggleRetweets(twitterState))
                .add(new ShowFollows(twitterState)).build();
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    protected Set<String> requiredConfigurationProperties() {
        return newHashSet(CHANNEL, OAUTH_ACCESS_TOKEN, OAUTH_ACCESS_TOKEN_SECRET, OAUTH_CONSUMER_KEY, OAUTH_CONSUMER_SECRET);
    }

    private Configuration createTwitterConfiguration() {
        return new ConfigurationBuilder()
                .setDebugEnabled(config.getBoolean("debug", false))
                .setOAuthConsumerKey(config.getString(OAUTH_CONSUMER_KEY))
                .setOAuthConsumerSecret(config.getString(OAUTH_CONSUMER_SECRET))
                .setOAuthAccessToken(config.getString(OAUTH_ACCESS_TOKEN))
                .setOAuthAccessTokenSecret(config.getString(OAUTH_ACCESS_TOKEN_SECRET))
                .setIncludeRTsEnabled(false)
                .setUserStreamRepliesAllEnabled(false)
                .setIncludeMyRetweetEnabled(false)
                .setUseSSL(true)
                .build();
    }
}
