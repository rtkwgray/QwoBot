package com.sl5r0.qwobot.irc.service.twitter;

import org.slf4j.Logger;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterException;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * StatusListener that filters out replies and retweets.
 */
public class NewTweetListener extends StatusAdapter {
    private static final Logger log = getLogger(NewTweetListener.class);
    private final TwitterService twitterService;

    public NewTweetListener(TwitterService twitterService) {
        this.twitterService = checkNotNull(twitterService, "twitterService must not be null");
    }

    @Override
    public void onStatus(Status status) {
        final boolean statusIsReply = (status.getInReplyToStatusId() != -1 || status.getInReplyToUserId() != -1 || status.getInReplyToScreenName() != null);
        if (!status.isRetweet() && !statusIsReply) {
            twitterService.tweetReceived(status);
        }
    }

    @Override
    public void onException(Exception e) {
        if (e instanceof TwitterException) {
            if (((TwitterException) e).isCausedByNetworkIssue()) {
                log.warn("Couldn't contact Twitter due to a network issue", e);
            } else {
                log.error("Couldn't contact Twitter for some reason (credentials are probably incorrect)", e);
            }
        } else {
            log.error("Unknown Twitter exception occurred", e);
        }
    }
}
