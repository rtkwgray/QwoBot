package com.sl5r0.qwobot.plugins.twitter;

import com.google.common.base.Joiner;
import com.google.common.primitives.Longs;
import com.sl5r0.qwobot.core.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;

import java.util.Map;
import java.util.Set;

import static com.google.api.client.util.Maps.newHashMap;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.sl5r0.qwobot.core.Format.BLUE;

public class TwitterState {
    private static final Logger log = LoggerFactory.getLogger(TwitterState.class);
    private static final String FOLLOWS_SEPARATOR = ", ";
    private final Twitter twitter;
    private final TwitterStream twitterStream;
    private final Map<Long, String> follows = newHashMap();
    private boolean showingRetweets = false;
    private boolean showingReplies = false;
    private Format tweetColor = BLUE;


    TwitterState(Twitter twitter, TwitterStream twitterStream) {
        this.twitter = twitter;
        this.twitterStream = twitterStream;
    }

    void follow(String handle) throws TwitterException {
        final User user = getTwitterUserFromHandle(handle);
        follows.put(user.getId(), user.getScreenName());
        log.info("Followed user: " + user.getScreenName());
    }

    void unfollow(String handle) throws TwitterException {
        final User user = getTwitterUserFromHandle(handle);
        follows.remove(user.getId());
        log.info("Unfollowed user: " + user.getScreenName());
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

    Format getTweetColor() {
        return tweetColor;
    }

    void setTweetColor(Format tweetColor) {
        this.tweetColor = tweetColor;
    }

    Set<String> getFollowHandles() {
        return copyOf(follows.values());
    }

    void restartStream() {
        if (!follows.isEmpty()) {
            twitterStream.filter(new FilterQuery(Longs.toArray(follows.keySet())));
        }
    }

    private User getTwitterUserFromHandle(String handle) throws TwitterException {
        return twitter.showUser(handle);
    }

    String followsToString() {
        return Joiner.on(FOLLOWS_SEPARATOR).join(follows.values());
    }
}
