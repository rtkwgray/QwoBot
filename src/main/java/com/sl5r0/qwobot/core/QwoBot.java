package com.sl5r0.qwobot.core;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.sl5r0.qwobot.plugins.PluginManager;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import java.io.IOException;

public class QwoBot extends PircBotX {
    private final EventBus eventBus = new EventBus();
    private final BotConfiguration config;
    private final PluginManager pluginManager;

    @Inject
    public QwoBot(BotConfiguration configuration, PluginManager pluginManager, EventBus eventBus) {
        this.config = configuration;
        this.pluginManager = pluginManager;
        this.getListenerManager().addListener(new QwoBotListener(eventBus));
    }

    public final void start() throws IrcException, IOException {
        this.setName(config.getString("bot.nick"));
        this.setLogin(config.getString("bot.nick"));
        this.setVerbose(config.getBoolean("options.verbose"));
        this.setAutoReconnect(config.getBoolean("options.autoReconnect"));
        this.connect(config.getString("server.host"), config.getInt("server.port"));
        this.joinChannel(config.getString("server.channel.name"));
        this.setMessageDelay(config.getLong("options.messageDelay"));
        pluginManager.initializePlugins();
    }
}
