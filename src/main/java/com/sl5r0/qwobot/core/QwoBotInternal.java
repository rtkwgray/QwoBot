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
import org.pircbotx.PircBotX;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class QwoBotInternal extends PircBotX implements QwoBot {
    private final EventBus eventBus = new EventBus();
    private final Set<Plugin> loadedPlugins = Sets.newHashSet();

    public QwoBotInternal() {
        super();
        this.getListenerManager().addListener(new QwoBotListener(eventBus));
        this.loadedPlugins.add(new Logger(this));
        this.loadedPlugins.add(new PluginInfo(this));
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

    private List<String> splitByNewline(String message) {
        return Lists.newArrayList(Splitter.on(Pattern.compile("\n")).split(message));
    }
}
