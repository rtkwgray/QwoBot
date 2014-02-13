package com.sl5r0.qwobot.plugins.reddit;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Sets;
import com.google.common.util.concurrent.RateLimiter;
import com.google.inject.Inject;
import com.sl5r0.qwobot.core.BotConfiguration;
import com.sl5r0.qwobot.plugins.Plugin;
import com.sl5r0.qwobot.plugins.commands.Command;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class Reddit extends Plugin {
    private static final Set<Command> commands = Sets.newHashSet();

    @Inject
    public Reddit(BotConfiguration config) {
        checkNotNull(config, "config cannot be null");
        config.setExpressionEngine(new XPathExpressionEngine());
        final HierarchicalConfiguration pluginConfig = getPluginConfiguration(config);
        final RedditRequestInitializer requestInitializer = new RedditRequestInitializer(pluginConfig.getString("username"));
        final RateLimiter rateLimiter = RateLimiter.create(0.5);
        final RedditSession redditSession = new RedditSession(new NetHttpTransport(), requestInitializer, rateLimiter, newSingleThreadExecutor());

        redditSession.login(pluginConfig.getString("username"), pluginConfig.getString("password"));
        commands.add(new PostLinkToReddit(redditSession, pluginConfig.getString("subreddit")));
    }

    @Override
    public Set<Command> getCommands() {
        return commands;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}