package com.sl5r0.qwobot.plugins.twitter;

import com.google.inject.Provider;
import com.sl5r0.qwobot.core.QwoBot;
import org.pircbotx.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

class TwitterListener implements StatusListener {
    private static final Logger log = LoggerFactory.getLogger(TwitterListener.class);
    private final String channel;
    private final TwitterState twitterState;
    private final Provider<QwoBot> botProvider;

    TwitterListener(TwitterState twitterState, String channel, Provider<QwoBot> botProvider) {
        this.channel = channel;
        this.twitterState = twitterState;
        this.botProvider = botProvider;
    }

    @Override
    public void onStatus(Status status) {
        // If this is a retweet and we're not showing retweets, don't send anything to the channel.
        if (!twitterState.isShowingRetweets() && status.isRetweet()) {
            return;
        }

        final boolean statusIsReply = (status.getInReplyToStatusId() != -1 || status.getInReplyToUserId() != -1 || status.getInReplyToScreenName() != null);
        if (!twitterState.isShowingReplies() && statusIsReply) {
            return;
        }

        // If we didn't filter out this message, send it to the channel.
        final Channel ircChannel = botProvider.get().getUserChannelDao().getChannel(channel);
        ircChannel.send().message(twitterState.getTweetColor().format(status.getUser().getScreenName() + ": " + status.getText()));
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
        log.error("Twitter exception occurred", ex);
    }
}
