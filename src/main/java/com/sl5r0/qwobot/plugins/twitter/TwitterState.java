package com.sl5r0.qwobot.plugins.twitter;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.primitives.Longs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;

import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import static com.google.api.client.util.Maps.newHashMap;
import static com.google.common.collect.ImmutableSet.copyOf;
import static java.util.prefs.Preferences.systemNodeForPackage;
import static org.pircbotx.Colors.BLUE;

public class TwitterState {
    private static final Logger log = LoggerFactory.getLogger(TwitterState.class);
    private static final String FOLLOWS_PREFERENCE = "follows";
    private static final String RETWEETS_PREFERENCE = "showingRetweets";
    private static final String REPLIES_PREFERENCE = "showingReplies";
    private static final String FOLLOWS_SEPARATOR = ", ";
    private final Twitter twitter;
    private final TwitterStream twitterStream;
    private final Map<Long, String> follows = newHashMap();
    private final Preferences preferences = systemNodeForPackage(TwitterState.class);
    private boolean showingRetweets = false;
    private boolean showingReplies = false;
    private String tweetColor = BLUE;


    TwitterState(Twitter twitter, TwitterStream twitterStream) {
        this.twitter = twitter;
        this.twitterStream = twitterStream;
        loadSettings();
    }

    void follow(String handle) throws TwitterException {
        final User user = getTwitterUserFromHandle(handle);
        follows.put(user.getId(), user.getScreenName());
        log.info("Followed user: " + user.getScreenName());
        saveSettings();
    }

    void unfollow(String handle) throws TwitterException {
        final User user = getTwitterUserFromHandle(handle);
        follows.remove(user.getId());
        log.info("Unfollowed user: " + user.getScreenName());
        saveSettings();
    }

    boolean isShowingReplies() {
        return showingReplies;
    }

    void setShowingReplies(boolean showingReplies) {
        this.showingReplies = showingReplies;
        saveSettings();
    }

    boolean isShowingRetweets() {
        return showingRetweets;
    }

    void setShowingRetweets(boolean showingRetweets) {
        this.showingRetweets = showingRetweets;
        saveSettings();
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

    void restartStream() {
        if (!follows.isEmpty()) {
            twitterStream.filter(new FilterQuery(Longs.toArray(follows.keySet())));
        }
    }

    private User getTwitterUserFromHandle(String handle) throws TwitterException {
        return twitter.showUser(handle);
    }

    private void saveSettings() {
        preferences.put(FOLLOWS_PREFERENCE, followsToString());
        preferences.putBoolean(RETWEETS_PREFERENCE, showingRetweets);
        preferences.putBoolean(REPLIES_PREFERENCE, showingReplies);
    }

    private void loadSettings() {
        showingRetweets = preferences.getBoolean(RETWEETS_PREFERENCE, false);
        showingReplies = preferences.getBoolean(REPLIES_PREFERENCE, false);

        final String savedFollows = preferences.get(FOLLOWS_PREFERENCE, "");
        final Iterable<String> follows = Splitter.on(FOLLOWS_SEPARATOR).omitEmptyStrings().trimResults().split(savedFollows);
        for (String follow : follows) {
            try {
                follow(follow);
            } catch (TwitterException e) {
                log.warn("Couldn't refollow: " + follow, e);
            }
        }

        restartStream();
    }

    String followsToString() {
        return Joiner.on(FOLLOWS_SEPARATOR).join(follows.values());
    }
}
