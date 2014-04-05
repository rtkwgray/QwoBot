package com.sl5r0.qwobot.irc.service.twitter;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.irc.service.MessageDispatcher;
import com.sl5r0.qwobot.irc.service.runnables.MessageRunnable;
import com.sl5r0.qwobot.persistence.SettingsRepository;
import org.apache.commons.configuration.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.primitives.Longs.toArray;
import static com.sl5r0.qwobot.core.IrcTextFormatter.BLUE;
import static com.sl5r0.qwobot.irc.service.MessageDispatcher.startingWith;
import static com.sl5r0.qwobot.util.ExtraPredicates.matchesCaseInsensitiveString;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class TwitterService extends AbstractIdleService {
    private static final Logger log = getLogger(TwitterService.class);
    private static final String OAUTH_CONSUMER_KEY = "twitter.oauth.consumer-key";
    private static final String OAUTH_CONSUMER_SECRET = "twitter.oauth.consumer-secret";
    private static final String OAUTH_ACCESS_TOKEN = "twitter.oauth.access-token";
    private static final String OAUTH_ACCESS_TOKEN_SECRET = "twitter.oauth.access-token-secret";
    private static final String CHANNEL = "twitter.channel";

    private final PircBotX bot;
    private final SettingsRepository settingsRepository;
    private final MessageDispatcher messageDispatcher;
    private final EventBus eventBus;
    private TwitterStream stream;
    private Twitter twitter;
    private String channel;

    private final BiMap<Long, String> following = HashBiMap.create();

    @Inject
    public TwitterService(Provider<PircBotX> botProvider, MessageDispatcher messageDispatcher, EventBus eventBus, SettingsRepository settingsRepository, Configuration configuration) {
        this.settingsRepository = checkNotNull(settingsRepository, "settingsRepository must not be null");
        this.messageDispatcher = checkNotNull(messageDispatcher, "messageDispatcher must not be null");
        this.eventBus = checkNotNull(eventBus, "eventBus must not be null");
        this.bot = botProvider.get();

        if (configurationIsValid(configuration)) {
            this.messageDispatcher
                    .subscribeToMessage(startingWith("!following"), new ShowFollows())
                    .subscribeToMessage(startingWith("!follow"), new FollowUser())
                    .subscribeToMessage(startingWith("!unfollow"), new UnfollowUser());

            final twitter4j.conf.Configuration twitterConfig = twitterConfig(configuration);
            this.channel = configuration.getString(CHANNEL);
            this.stream = new TwitterStreamFactory(twitterConfig).getInstance();
            this.twitter = new TwitterFactory(twitterConfig).getInstance();
            this.stream.addListener(new NewTweetListener(this));
        } else {
            log.error("Twitter configuration missing or invalid.");
        }
    }

    private void updateStreamFilter() {
        log.debug("Restarting twitter status stream.");
        if (following.isEmpty()) {
            stream.shutdown();
        } else {
            stream.filter(new FilterQuery(toArray(following.keySet())));
        }
    }

    @Override
    protected void startUp() throws Exception {
        eventBus.register(messageDispatcher);
        updateStreamFilter();
    }

    @Override
    protected void shutDown() throws Exception {
        eventBus.unregister(messageDispatcher);
        stream.shutdown();
    }

    public void tweetReceived(Status status) {
        final String tweetString = BLUE.format(status.getUser().getScreenName() + ": " + status.getText());
        bot.getUserChannelDao().getChannel(channel).send().message(tweetString);
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

    private class FollowUser implements MessageRunnable {
        @Override
        public void run(GenericMessageEvent<PircBotX> event, List<String> arguments) {
            if (arguments.size() >= 2) {
                final String handle = arguments.get(1);
                if (!getFollow(handle).isPresent()) {
                    final User user;
                    try {
                        user = twitter.showUser(handle);
                        following.put(user.getId(), user.getScreenName());
                        updateStreamFilter();
                        event.respond("Added " + user.getScreenName() + " to the list of followed users.");
                    } catch (TwitterException e) {
                        event.respond("Sorry, I couldn't find a Twitter user with that name.");
                    }
                }
            }
        }
    }

    private class UnfollowUser implements MessageRunnable {
        @Override
        public void run(GenericMessageEvent<PircBotX> event, List<String> arguments) {
            if (arguments.size() >= 2) {
                final String handle = arguments.get(1);
                final Optional<String> follow = getFollow(handle);
                if (follow.isPresent()) {
                    log.info("Removing " + follow.get() + " from follows.");
                    following.inverse().remove(follow.get());
                    updateStreamFilter();
                    event.respond("Removed " + follow.get() + " from the list of followed users.");
                } else {
                    event.respond("It doesn't look like I'm following them.");
                }
            }
        }
    }

    private class ShowFollows implements MessageRunnable {
        @Override
        public void run(GenericMessageEvent<PircBotX> event, List<String> arguments) {
            if (arguments.size() >= 2) {
                final String follows = Joiner.on(", ").join(following.values());
                event.respond("I'm currently following: " + follows);
            }
        }
    }

    private Optional<String> getFollow(String handle) {
        return tryFind(following.values(), matchesCaseInsensitiveString(handle));
    }

    private boolean configurationIsValid(Configuration configuration) {
        return configuration.containsKey(CHANNEL)
                && configuration.containsKey(OAUTH_CONSUMER_KEY)
                && configuration.containsKey(OAUTH_ACCESS_TOKEN)
                && configuration.containsKey(OAUTH_ACCESS_TOKEN_SECRET)
                && configuration.containsKey(OAUTH_CONSUMER_SECRET);
    }
}
