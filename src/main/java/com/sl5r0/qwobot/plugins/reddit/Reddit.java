package com.sl5r0.qwobot.plugins.reddit;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.RateLimiter;
import com.sl5r0.qwobot.plugins.ConfigurablePlugin;
import com.sl5r0.qwobot.plugins.commands.Command;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class Reddit extends ConfigurablePlugin {

    @Override
    public Set<Command> getCommands() {
        final RateLimiter rateLimiter = RateLimiter.create(0.5);
        final RedditRequestInitializer requestInitializer = new RedditRequestInitializer(config.getString("username"));
        final RedditSession redditSession = new RedditSession(new NetHttpTransport(), requestInitializer, rateLimiter,
                config.getString("username"), config.getString("password"));

        return ImmutableSet.<Command>of(new PostLinkToReddit(redditSession, config.getString("subreddit")));
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    protected Set<String> requiredConfigurationProperties() {
        return newHashSet("username", "password", "subreddit");
    }
}