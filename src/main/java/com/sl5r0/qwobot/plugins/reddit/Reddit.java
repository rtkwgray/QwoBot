package com.sl5r0.qwobot.plugins.reddit;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Sets;
import com.sl5r0.qwobot.plugins.Plugin;
import com.sl5r0.qwobot.plugins.commands.Command;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.Set;

public class Reddit extends Plugin {
    private static final Set<Command> commands = Sets.newHashSet();

    public Reddit(HierarchicalConfiguration config) {
        final RedditSession redditSession = new RedditSession(new NetHttpTransport());
        redditSession.login(config.getString("username"), config.getString("password"));
        commands.add(new PostLinkToReddit(redditSession, config.getString("subreddit")));
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