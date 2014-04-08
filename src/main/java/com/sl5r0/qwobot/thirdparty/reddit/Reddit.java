package com.sl5r0.qwobot.thirdparty.reddit;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.plugins.exceptions.LoginFailedException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;

import static com.sl5r0.qwobot.guice.ConfigurationProvider.readConfigurationValue;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class Reddit {
    private static final Logger log = getLogger(Reddit.class);

    private final RedditSession redditSession;
    private final Optional<String> subReddit;

    @Inject
    public Reddit(HierarchicalConfiguration configuration) {
        this.subReddit = readConfigurationValue(configuration, "reddit.subreddit");
        this.redditSession = new RedditSession();

        final Optional<String> username = readConfigurationValue(configuration, "reddit.username");
        final Optional<String> password = readConfigurationValue(configuration, "reddit.password");
        if (username.isPresent() && password.isPresent() && subReddit.isPresent()) {
            try {
                this.redditSession.login(username.get(), password.get());
            } catch (LoginFailedException e) {
                log.error("Reddit authentication failed for user \"" + username.get() + "\"");
            }
        } else {
            log.error("Reddit integration has been disabled because configuration is missing or incorrect.");
        }
    }

    public void post(String title, String url) throws RedditPostFailedException {
        if (redditSession.isAuthenticated()) {
            try {
                redditSession.postLink(subReddit.get(), title, URI.create(url));
            } catch (IOException | IllegalStateException e) {
                throw new RedditPostFailedException(e);
            }
        } else {
            log.debug("Not posting " + url + " to Reddit because I'm not logged in.");
        }
    }
}