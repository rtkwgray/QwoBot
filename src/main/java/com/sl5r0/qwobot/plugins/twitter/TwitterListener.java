package com.sl5r0.qwobot.plugins.twitter;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

class TwitterListener implements StatusListener {
    private static final Logger log = LoggerFactory.getLogger(TwitterListener.class);
    private final Channel channel;
    private final TwitterState twitterState;
    private String tweetColor = Colors.BLUE;

    TwitterListener(TwitterState twitterState, Channel channel) {
        this.channel = channel;
        this.twitterState = twitterState;
    }

    @Override
    public void onStatus(Status status) {
        // Don't display messages that aren't from people we're following.
        if (twitterState.getFollows().contains(status.getUser().getId())) {
            channel.sendMessage(tweetColor + status.getUser().getScreenName() + ": " + status.getText());
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
        log.error("Twitter exception occurred", ex);
    }

    public void setTweetColor(String tweetColor) {
        this.tweetColor = tweetColor;
    }
}
