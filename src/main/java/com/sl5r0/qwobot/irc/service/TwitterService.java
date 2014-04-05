package com.sl5r0.qwobot.irc.service;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.persistence.SettingsRepository;
import com.sl5r0.qwobot.plugins.twitter.TwitterListener;
import com.sl5r0.qwobot.plugins.twitter.TwitterState;
import org.apache.commons.configuration.Configuration;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class TwitterService extends AbstractIdleService {
    private static final String OAUTH_CONSUMER_KEY = "twitter.oauth.consumer-key";
    private static final String OAUTH_CONSUMER_SECRET = "twitter.oauth.consumer-secret";
    private static final String OAUTH_ACCESS_TOKEN = "twitter.oauth.access-token";
    private static final String OAUTH_ACCESS_TOKEN_SECRET = "twitter.oauth.access-token-secret";
    private static final String CHANNEL = "twitter.channel";

    private final SettingsRepository settingsRepository;
    private final MessageDispatcher messageDispatcher;
    private final EventBus eventBus;

    @Inject
    public TwitterService(MessageDispatcher messageDispatcher, EventBus eventBus, SettingsRepository settingsRepository, Configuration configuration) {
        this.settingsRepository = checkNotNull(settingsRepository, "settingsRepository must not be null");
        this.eventBus = checkNotNull(eventBus, "eventBus must not be null");
        this.messageDispatcher = checkNotNull(messageDispatcher, "messageDispatcher must not be null");

        twitter4j.conf.Configuration twitterConfig = twitterConfig(configuration);
        final TwitterStream twitterStream = new TwitterStreamFactory(twitterConfig).getInstance();
        final Twitter twitter = new TwitterFactory(twitterConfig).getInstance();
        TwitterState twitterState = new TwitterState(twitter, twitterStream);
        twitterStream.addListener(new TwitterListener(twitterState));
    }

    @Override
    protected void startUp() throws Exception {
        eventBus.register(messageDispatcher);
    }

    @Override
    protected void shutDown() throws Exception {
        eventBus.unregister(messageDispatcher);
    }

    private twitter4j.conf.Configuration twitterConfig(Configuration configuration) {
        return new ConfigurationBuilder()
                .setDebugEnabled(configuration.getBoolean("debug", false))
                .setOAuthConsumerKey(configuration.getString(OAUTH_CONSUMER_KEY))
                .setOAuthConsumerSecret(configuration.getString(OAUTH_CONSUMER_SECRET))
                .setOAuthAccessToken(configuration.getString(OAUTH_ACCESS_TOKEN))
                .setOAuthAccessTokenSecret(configuration.getString(OAUTH_ACCESS_TOKEN_SECRET))
                .setIncludeRTsEnabled(false)
                .setUserStreamRepliesAllEnabled(false)
                .setIncludeMyRetweetEnabled(false)
                .setUseSSL(true)
                .build();
    }
}
