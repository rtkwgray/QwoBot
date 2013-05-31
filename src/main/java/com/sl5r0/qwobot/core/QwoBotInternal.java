package com.sl5r0.qwobot.core;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.sl5r0.qwobot.api.Plugin;
import com.sl5r0.qwobot.api.QwoBot;
import com.sl5r0.qwobot.api.QwoBotPlugin;
import com.sl5r0.qwobot.domain.Channel;
import com.sl5r0.qwobot.domain.User;
import com.sl5r0.qwobot.plugins.Logger;
import com.sl5r0.qwobot.plugins.PluginInfo;
import com.sl5r0.qwobot.plugins.Twitter;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class QwoBotInternal extends PircBotX implements QwoBot {
    private final EventBus eventBus = new EventBus();
    private final Set<Plugin> loadedPlugins = Sets.newHashSet();
    private final HierarchicalConfiguration config;

    public QwoBotInternal(HierarchicalConfiguration config) {
        super();
        this.config = config;
        this.loadedPlugins.add(new Logger(this));
        this.loadedPlugins.add(new PluginInfo(this));
        this.loadedPlugins.add(new Twitter(this));
        this.getListenerManager().addListener(new QwoBotListener(eventBus));
    }

    public final void start() throws IrcException, IOException {
        this.setName(config.getString("bot.nick"));
        this.setLogin(config.getString("bot.nick"));
        this.setVerbose(config.getBoolean("options.verbose"));
        this.setAutoReconnect(config.getBoolean("options.autoReconnect"));
        this.connect(config.getString("server.host"), config.getInt("server.port"));
        this.joinChannel(config.getString("server.channel.name"));

        // TODO: move this to configuration value.
        this.setMessageDelay(250);
    }

    @Override
    public void registerPlugin(QwoBotPlugin qwoBotPlugin) {
        eventBus.register(qwoBotPlugin);
    }

    @Override
    public User getUserDetails(String nick) {
        return new User(getUser(nick));
    }

    @Override
    public Channel getChannelDetails(String name) {
        return new Channel(getChannel(name));
    }

    @Override
    public Set<Plugin> getLoadedPlugins() {
        return ImmutableSet.copyOf(loadedPlugins);
    }

    @Override
    public void sendMessageToUser(User user, String message) {
        for (String line : splitByNewline(message)) {
            sendMessage(getUser(user.nick), line);
        }
    }

    @Override
    public void sendMessageToChannel(Channel channel, String message) {
        for (String line : splitByNewline(message)) {
            sendMessage(getChannel(channel.name), line);
        }
    }

    @Override
    public void sendMessageToAllChannels(String message) {
        sendMessage(config.getString("server.channel.name"), message);
    }

    private List<String> splitByNewline(String message) {
        return Lists.newArrayList(Splitter.on(Pattern.compile("\n")).split(message));
    }
}
