package com.sl5r0.qwobot.core;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.plugins.PluginManager;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import java.io.IOException;

@Singleton
public class QwoBot extends PircBotX {
    private final PluginManager pluginManager;

    @Inject
    public QwoBot(BotConfiguration configuration, PluginManager pluginManager, EventBus eventBus) {
        super(configuration.toPircBotXConfiguration(new QwoBotListener(eventBus)));
        this.pluginManager = pluginManager;
    }

    public void start() throws IrcException, IOException {
        this.connect();
        pluginManager.initializePlugins();
    }
}
