package com.sl5r0.qwobot.plugins.twitter;

import com.google.common.primitives.Longs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;

import java.util.Map;
import java.util.Set;

import static com.google.api.client.util.Maps.newHashMap;
import static com.google.common.collect.ImmutableSet.copyOf;
import static org.pircbotx.Colors.BLUE;

public class TwitterState {
    private static final Logger log = LoggerFactory.getLogger(TwitterState.class);
    private final Twitter twitter;
    private final TwitterStream twitterStream;
    private final Map<Long, String> follows = newHashMap();
    private boolean showingRetweets = false;
    private boolean showingReplies = false;
    private String tweetColor = BLUE;

    TwitterState(Twitter twitter, TwitterStream twitterStream) {
        this.twitter = twitter;
        this.twitterStream = twitterStream;
        listen();
    }

    void follow(String handle) throws TwitterException {
        final User user = getTwitterUserFromHandle(handle);
        follows.put(user.getId(), user.getName());
        log.info("Followed user: " + user.getName());
        listen();
    }

    void unfollow(String handle) throws TwitterException {
        final User user = getTwitterUserFromHandle(handle);
        follows.remove(user.getId());
        log.info("Unfollowed user: " + user.getName());
        listen();
    }

    boolean isShowingReplies() {
        return showingReplies;
    }

    void setShowingReplies(boolean showingReplies) {
        this.showingReplies = showingReplies;
    }

    boolean isShowingRetweets() {
        return showingRetweets;
    }

    void setShowingRetweets(boolean showingRetweets) {
        this.showingRetweets = showingRetweets;
    }

    String getTweetColor() {
        return tweetColor;
    }

    void setTweetColor(String tweetColor) {
        this.tweetColor = tweetColor;
    }

    Set<String> getFollowHandles() {
        return copyOf(follows.values());
    }

    private void listen() {
        if (!follows.isEmpty()) {
            twitterStream.filter(new FilterQuery(Longs.toArray(follows.keySet())));
        }
    }

    private User getTwitterUserFromHandle(String handle) throws TwitterException {
        return twitter.showUser(handle);
    }
}
