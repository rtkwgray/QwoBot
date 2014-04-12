package com.sl5r0.qwobot.irc.service.twitter;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.core.IrcTextFormatter;
import com.sl5r0.qwobot.domain.TwitterFollow;
import com.sl5r0.qwobot.domain.help.Command;
import com.sl5r0.qwobot.irc.service.AbstractIrcEventService;
import com.sl5r0.qwobot.irc.service.IrcBotService;
import com.sl5r0.qwobot.persistence.SimpleRepository;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
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
import static com.sl5r0.qwobot.util.ExtraPredicates.matchesCaseInsensitiveString;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class TwitterService extends AbstractIrcEventService {
    private static final Logger log = getLogger(TwitterService.class);
    private static final Command followCommand = new Command("!twitter:follow", "Follow one or more users").addUnboundedParameter("twitter handle");
    private static final Command followingCommand = new Command("!twitter:following", "Show currently followed users");
    private static final Command unfollowCommand = new Command("!twitter:unfollow", "Unfollow one or more users").addUnboundedParameter("twitter handle");
    private static final Command colorCommand = new Command("!twitter:color", "Change the color of a user's tweets").addParameter("twitter handle").addParameter("color");
    private static final String OAUTH_CONSUMER_KEY = "twitter.oauth.consumer-key";
    private static final String OAUTH_CONSUMER_SECRET = "twitter.oauth.consumer-secret";
    private static final String OAUTH_ACCESS_TOKEN = "twitter.oauth.access-token";
    private static final String OAUTH_ACCESS_TOKEN_SECRET = "twitter.oauth.access-token-secret";
    private static final String CHANNEL = "twitter.channel";

    private final PircBotX bot;
    private final SimpleRepository<TwitterFollow> twitterRepository;
    private Optional<TwitterStream> stream = absent();
    private Twitter twitter;
    private String channel;

    private final Set<TwitterFollow> following = newHashSet();

    @Inject
    public TwitterService(IrcBotService ircBotService, SimpleRepository<TwitterFollow> twitterRepository, HierarchicalConfiguration configuration) {
        super(newHashSet(followCommand, followingCommand, unfollowCommand, colorCommand));
        this.twitterRepository = checkNotNull(twitterRepository, "twitterRepository must not be null");
        this.bot = ircBotService.getBot();

        if (configurationIsValid(configuration)) {
            initializeTwitter(configuration);
            loadTwitterFollows();
        } else {
            log.error("Twitter integration has been disabled because configuration missing or invalid.");
        }
    }

    public void tweetReceived(Status status) {
        final Optional<TwitterFollow> follow = getTwitterFollow(status.getUser().getScreenName());
        if (follow.isPresent()) {
            final String tweetString = follow.get().getStatusColor().format("@" + status.getUser().getScreenName() + ": " + status.getText().replace('\n', ' '));
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
    protected void doStart() {
        if (stream.isPresent()) {
            super.doStart();
            updateStreamFilter();
        } else {
            log.warn("Not starting service because configuration is invalid.");
            this.stopAsync();
        }
    }

    @Override
    protected void doStop() {
        if (stream.isPresent()) {
            stream.get().shutdown();
        }
        super.doStop();
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

    @Subscribe
    public void run(MessageEvent<PircBotX> event) {
        final List<String> arguments  = argumentsFor(followCommand, event.getMessage());
        final String handle = arguments.get(0).replace("@", "");
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

    @Subscribe
    public void unfollowUser(MessageEvent<PircBotX> event) {
        final List<String> arguments = argumentsFor(unfollowCommand, event.getMessage());
        final String handle = arguments.get(0).replace("@", "");
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

    @Subscribe
    public void showFollows(MessageEvent<PircBotX> event) {
        argumentsFor(followingCommand, event.getMessage());
        if (following.isEmpty()) {
            event.respond("I'm not following anybody!");
        } else {
            final String follows = Joiner.on(", ").join(transform(following, toPrettyString));
            event.respond("I'm currently following: " + follows);
        }
    }

    @Subscribe
    public void changeTweetColor(MessageEvent<PircBotX> event) {
        final List<String> arguments = argumentsFor(colorCommand, event.getMessage());
        final IrcTextFormatter newStatusColor;
        try {
            newStatusColor = IrcTextFormatter.valueOf(arguments.get(1).toUpperCase());
        } catch (IllegalArgumentException e) {
            event.respond("I don't understand that color.");
            return;
        }

        final String handle = arguments.get(0).replace("@", "");
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

    private Optional<TwitterFollow> getTwitterFollow(final String handle) {
        return tryFind(following, new Predicate<TwitterFollow>() {
            @Override
            public boolean apply(TwitterFollow input) {
                return matchesCaseInsensitiveString(handle).apply(input.getHandle());
            }
        });
    }
}
