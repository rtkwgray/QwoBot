package com.sl5r0.qwobot.thirdparty.reddit;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class Reddit {
    private static final Logger log = getLogger(Reddit.class);

    private final RedditSession redditSession;

    private String username;
    private String password;
    private String subReddit;

    @Inject
    public Reddit(Configuration configuration) {
        try {
            this.username = readConfigurationValue(configuration, "reddit.username");
            this.password = readConfigurationValue(configuration, "reddit.password");
            this.subReddit = readConfigurationValue(configuration, "reddit.subreddit");
        } catch (NullPointerException e) {
            log.error("Reddit integration has been disabled. Couldn't read configuration value: " + e.getMessage());
        }

        this.redditSession = new RedditSession();

        try {
            this.redditSession.login(username, password);
        } catch (Exception e) {
            log.error("Couldn't login to reddit.", e);
        }
    }

    public void post(String title, String url) throws RedditPostFailedException {
        try {
            redditSession.postLink(subReddit, title, URI.create(url));
        } catch (IOException | IllegalStateException e) {
            throw new RedditPostFailedException(e);
        }
    }

    private String readConfigurationValue(Configuration configuration, String key) {
        return checkNotNull(configuration.getString(key), key);
    }
}