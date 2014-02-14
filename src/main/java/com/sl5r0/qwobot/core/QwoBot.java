package com.sl5r0.qwobot.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.plugins.PluginManager;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Singleton
public class QwoBot extends PircBotX {
    private static final Logger log = LoggerFactory.getLogger(QwoBot.class);
    private final PluginManager pluginManager;

    @Inject
    public QwoBot(BotConfiguration configuration, PluginManager pluginManager) {
        super(configuration.toPircBotXConfiguration());
        this.pluginManager = pluginManager;
    }

    public void start() throws IrcException, IOException {
        pluginManager.initializePlugins();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connect();
                } catch (IOException | IrcException e) {
                   log.error("Couldn't start bot.", e);
                }
            }
        }).start();
    }
}
