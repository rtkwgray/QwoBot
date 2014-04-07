package com.sl5r0.qwobot.irc.service.twitter;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.core.IrcTextFormatter;
import com.sl5r0.qwobot.domain.TwitterFollow;
import com.sl5r0.qwobot.irc.service.IrcBotService;
import com.sl5r0.qwobot.irc.service.MessageDispatcher;
import com.sl5r0.qwobot.irc.service.runnables.MessageRunnable;
import com.sl5r0.qwobot.persistence.SimpleRepository;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.primitives.Longs.toArray;
import static com.sl5r0.qwobot.core.IrcTextFormatter.BLUE;
import static com.sl5r0.qwobot.domain.TwitterFollow.*;
import static com.sl5r0.qwobot.guice.ConfigurationProvider.readConfigurationValue;
import static com.sl5r0.qwobot.irc.service.MessageDispatcher.startingWithTrigger;
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
    private final SimpleRepository<TwitterFollow> twitterRepository;
    private final MessageDispatcher messageDispatcher;
    private final EventBus eventBus;
    private Optional<TwitterStream> stream = absent();
    private Twitter twitter;
    private String channel;

    private final Set<TwitterFollow> following = newHashSet();

    @Inject
    public TwitterService(IrcBotService ircBotService, MessageDispatcher messageDispatcher, EventBus eventBus, SimpleRepository<TwitterFollow> twitterRepository, HierarchicalConfiguration configuration) {
        this.twitterRepository = checkNotNull(twitterRepository, "twitterRepository must not be null");
        this.messageDispatcher = checkNotNull(messageDispatcher, "messageDispatcher must not be null");
        this.eventBus = checkNotNull(eventBus, "eventBus must not be null");
        this.bot = ircBotService.getBot();

        if (configurationIsValid(configuration)) {
            this.messageDispatcher
                    .subscribeToMessage(startingWithTrigger("!following"), new ShowFollows())
                    .subscribeToMessage(startingWithTrigger("!follow"), new FollowUser())
                    .subscribeToMessage(startingWithTrigger("!statuscolor"), new ChangeStatusColor())
                    .subscribeToMessage(startingWithTrigger("!unfollow"), new UnfollowUser());

            initializeTwitter(configuration);
            loadTwitterFollows();
        } else {
            log.error("Twitter integration has been disabled because configuration missing or invalid.");
        }
    }

    public void tweetReceived(Status status) {
        final Optional<TwitterFollow> follow = getTwitterFollow(status.getUser().getScreenName());
        if (follow.isPresent()) {
            final String tweetString = follow.get().getStatusColor().format("@" + status.getUser().getScreenName() + ": " + status.getText());
            bot.getUserChannelDao().getChannel(channel).send().message(tweetString);
        }
    }

    private void initializeTwitter(HierarchicalConfiguration configuration) {
        final twitter4j.conf.Configuration twitterConfig = twitterConfig(configuration);
        this.channel = configuration.getString(CHANNEL);
        this.twitter = new TwitterFactory(twitterConfig).getInstance();
        this.stream = of(new TwitterStreamFactory(twitterConfig).getInstance());
        this.stream.get().addListener(new NewTweetListener(this));
    }

    private boolean configurationIsValid(HierarchicalConfiguration configuration) {
        final Optional<String> channel = readConfigurationValue(configuration, CHANNEL);
        final Optional<String> oathConsumerKey = readConfigurationValue(configuration, OAUTH_CONSUMER_KEY);
        final Optional<String> oathAccessToken = readConfigurationValue(configuration, OAUTH_ACCESS_TOKEN);
        final Optional<String> oathAccessTokenSecret = readConfigurationValue(configuration, OAUTH_ACCESS_TOKEN_SECRET);
        final Optional<String> oathConsumerSecret = readConfigurationValue(configuration, OAUTH_CONSUMER_SECRET);

        return channel.isPresent()
                && oathAccessToken.isPresent()
                && oathConsumerSecret.isPresent()
                && oathConsumerKey.isPresent()
                && oathAccessTokenSecret.isPresent();
    }

    private void loadTwitterFollows() {
        try {
            final List<TwitterFollow> existingFollows = twitterRepository.findAll(TwitterFollow.class);
            following.addAll(existingFollows);
            log.info("Loaded " + existingFollows.size() + " existing Twitter follows: " + Joiner.on(", ").join(transform(existingFollows, toHandle)));
        } catch (RuntimeException e) {
            log.error("Couldn't load existing Twitter follows", e);
        }
    }

    private void updateStreamFilter() {
        log.debug("Restarting twitter status stream.");
        if (following.isEmpty()) {
            stream.get().shutdown();
        } else {
            stream.get().filter(new FilterQuery(toArray(transform(following, toTwitterId))));
        }
    }

    @Override
    protected void startUp() throws Exception {
        if (stream.isPresent()) {
            eventBus.register(messageDispatcher);
            log.info("Starting service.");
            updateStreamFilter();
        } else {
            log.warn("Not starting service because configuration is invalid.");
            this.stopAsync();
        }
    }

    @Override
    protected void shutDown() throws Exception {
        if (stream.isPresent()) {
            eventBus.unregister(messageDispatcher);
            stream.get().shutdown();
        }
    }

    private twitter4j.conf.Configuration twitterConfig(Configuration configuration) {
        return new ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey(configuration.getString(OAUTH_CONSUMER_KEY))
                .setOAuthConsumerSecret(configuration.getString(OAUTH_CONSUMER_SECRET))
                .setOAuthAccessToken(configuration.getString(OAUTH_ACCESS_TOKEN))
                .setOAuthAccessTokenSecret(configuration.getString(OAUTH_ACCESS_TOKEN_SECRET))
                .setIncludeRTsEnabled(false)
                .setUserStreamRepliesAllEnabled(false)
                .setIncludeMyRetweetEnabled(false)
                .setLoggerImpl("org.slf4j.LoggerFactory")
                .setUseSSL(true)
                .build();
    }

    private class FollowUser implements MessageRunnable {
        @Override
        public void run(GenericMessageEvent<PircBotX> event, List<String> arguments) {
            if (arguments.size() >= 2) {
                final String handle = arguments.get(1).replace("@", "");
                if (!getTwitterFollow(handle).isPresent()) {
                    try {
                        final User user = twitter.showUser(handle);
                        final TwitterFollow follow = new TwitterFollow(user.getId(), user.getScreenName(), BLUE);
                        if (!following.contains(follow)) {
                            following.add(follow);
                            twitterRepository.save(follow);
                            updateStreamFilter();
                            event.respond("Added " + toPrettyString.apply(follow) + " to the list of followed users.");
                        }
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
                final String handle = arguments.get(1).replace("@", "");
                final Optional<TwitterFollow> follow = getTwitterFollow(handle);
                if (follow.isPresent()) {
                    log.info("Removing " + follow.get().getHandle() + " from follows.");
                    following.remove(follow.get());
                    twitterRepository.delete(follow.get());
                    updateStreamFilter();
                    event.respond("Removed " + toPrettyString.apply(follow.get()) + " from the list of followed users.");
                } else {
                    event.respond("It doesn't look like I'm following them.");
                }
            }
        }
    }

    private class ShowFollows implements MessageRunnable {
        @Override
        public void run(GenericMessageEvent<PircBotX> event, List<String> arguments) {
            if (following.isEmpty()) {
                event.respond("I'm not following anybody!");
            } else {
                final String follows = Joiner.on(", ").join(transform(following, toPrettyString));
                event.respond("I'm currently following: " + follows);
            }
        }
    }

    private class ChangeStatusColor implements MessageRunnable {
        @Override
        public void run(GenericMessageEvent<PircBotX> event, List<String> arguments) {
            if (arguments.size() >= 3) {
                final IrcTextFormatter newStatusColor;
                try {
                    newStatusColor = IrcTextFormatter.valueOf(arguments.get(2).toUpperCase());
                } catch (IllegalArgumentException e) {
                    event.respond("I don't understand that color.");
                    return;
                }

                final String handle = arguments.get(1).replace("@", "");
                final Optional<TwitterFollow> follow = getTwitterFollow(handle);
                if (follow.isPresent()) {
                    log.info("Changed " + follow.get().getHandle() + "'s status update color to " + newStatusColor);
                    follow.get().setStatusColor(newStatusColor);
                    twitterRepository.saveOrUpdate(follow.get());
                    event.respond("Changed status update color for " + toPrettyString.apply(follow.get()));
                } else {
                    event.respond("It doesn't look like I'm following them.");
                }
            }
        }
    }

    private Optional<TwitterFollow> getTwitterFollow(final String handle) {
        return tryFind(following, new Predicate<TwitterFollow>() {
            @Override
            public boolean apply(TwitterFollow input) {
                return matchesCaseInsensitiveString(handle).apply(input.getHandle());
            }
        });
    }
}
