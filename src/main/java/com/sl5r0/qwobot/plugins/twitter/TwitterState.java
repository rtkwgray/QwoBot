package com.sl5r0.qwobot.plugins.twitter;

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Longs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.FilterQuery;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class TwitterState {
    private static final Logger log = LoggerFactory.getLogger(TwitterState.class);
    private final Twitter twitter;
    private final TwitterStream twitterStream;
    private final Set<Long> follows = newHashSet();

    TwitterState(Twitter twitter, TwitterStream twitterStream) {
        this.twitter = twitter;
        this.twitterStream = twitterStream;
        listen();
    }

    void follow(String handle) throws TwitterException {
        follows.add(getTwitterUserIdForHandle(handle));
        log.info("Followed user: " + handle);
        listen();
    }

    void unfollow(String handle) throws TwitterException {
        follows.remove(getTwitterUserIdForHandle(handle));
        log.info("Unfollowed user: " + handle);
        listen();
    }

    Set<Long> getFollows() {
        return ImmutableSet.copyOf(follows);
    }

    private void listen() {
        if (!follows.isEmpty()) {
            twitterStream.filter(new FilterQuery(Longs.toArray(follows)));
        }
    }

    private long getTwitterUserIdForHandle(String handle) throws TwitterException {
        return twitter.showUser(handle).getId();
    }
}
