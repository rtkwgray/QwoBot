package com.sl5r0.qwobot.plugins;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.common.primitives.Longs;
import com.sl5r0.qwobot.api.QwoBot;
import com.sl5r0.qwobot.api.QwoBotPlugin;
import com.sl5r0.qwobot.domain.MessageEvent;
import org.pircbotx.Colors;
import twitter4j.*;

import java.util.List;
import java.util.Set;

public class Twitter extends QwoBotPlugin {
    private static final long WR_RECORD_TWITTER_ID = 37104970;
    private static final long QW0RUM_TWITTER_ID = 1460330023;
    private static final String FOLLOW_TRIGGER = "!follow";
    private static final String UNFOLLOW_TRIGGER = "!unfollow";
    private static final String TWEET_COLOR = Colors.BLUE;
    private final Set<Long> following = Sets.newHashSet(WR_RECORD_TWITTER_ID, QW0RUM_TWITTER_ID);
    private final Set<String> triggers = Sets.newHashSet(FOLLOW_TRIGGER, UNFOLLOW_TRIGGER);
    private final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

    public Twitter(QwoBot qwoBot) {
        super(qwoBot);
        this.twitterStream.addListener(new TwitterListener());
        this.listen();
    }

    @Subscribe
    public void processMessageEvent(MessageEvent event) {
        final List<String> params = getParametersFromEvent(event);
        if (params.size() != 2 || !triggers.contains(params.get(0))) {
            return;
        }

        final String trigger = params.get(0);

        if(trigger == FOLLOW_TRIGGER) {
            final twitter4j.Twitter twitter = TwitterFactory.getSingleton();
            final String twitterHandle = params.get(1);

            long twitterId;
            try {
                twitterId = twitter.showUser(twitterHandle).getId();
            } catch (TwitterException e) {
                bot().sendMessageToAllChannels("Sorry, I couldn't add " + twitterHandle + " to my follows.");
                bot().sendMessageToAllChannels("The username may be invalid, or Twitter could be down.");
                return;
            }

            bot().sendMessageToAllChannels("Now following " + twitterHandle + " in this channel.");
            following.add(twitterId);
            listen();
        } else if(trigger == UNFOLLOW_TRIGGER) {
            final twitter4j.Twitter twitter = TwitterFactory.getSingleton();
            final String twitterHandle = params.get(1);

            long twitterId;
            try {
                twitterId = twitter.showUser(twitterHandle).getId();
            } catch (TwitterException e) {
                bot().sendMessageToAllChannels("Sorry, I can't find the twitter user " + twitterHandle + ".");
                bot().sendMessageToAllChannels("The username may be invalid, or Twitter could be down.");
                return;
            }

            if(!following.contains(twitterId)) {
                bot().sendMessageToAllChannels("Sorry, " + twitterHandle + " is not being followed in this channel.");
                return;
            }

            following.remove(twitterId);
            bot().sendMessageToAllChannels(twitterHandle + " has been unfollowed in this channel.");
            listen();
        }
    }

    public void listen() {
        twitterStream.filter(new FilterQuery(Longs.toArray(following)));
    }

    @Override
    public String getDescription() {
        return "Real-time twitter client.";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public String getHelp() {
        return "This plugin does not support any commands.";
    }

    private class TwitterListener implements StatusListener {
        @Override
        public void onStatus(Status status) {
            long twitterUserId = status.getUser().getId();
            if (following.contains(twitterUserId)) {
                bot().sendMessageToAllChannels(TWEET_COLOR + status.getUser().getScreenName() + ": " + status.getText());
            }
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        }

        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        }

        @Override
        public void onScrubGeo(long l, long l2) {
        }

        @Override
        public void onStallWarning(StallWarning stallWarning) {
        }

        @Override
        public void onException(Exception ex) {
            ex.printStackTrace();
        }
    }
}
