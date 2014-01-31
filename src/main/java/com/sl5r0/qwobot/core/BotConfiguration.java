package com.sl5r0.qwobot.core;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.pircbotx.Configuration;
import org.pircbotx.hooks.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;

public class BotConfiguration extends XMLConfiguration {
    private static final Logger log = LoggerFactory.getLogger(BotConfiguration.class);

    public BotConfiguration(File configFile) throws ConfigurationException {
        super(configFile);
        if (!configFile.exists()) {
            throw new ConfigurationException("Could not load configuration from " + configFile.getAbsolutePath());
        }

        log.info("Loaded configuration from " + configFile.getAbsolutePath());
        final Iterator<String> configurationKeys = getKeys();
        while (configurationKeys.hasNext()) {
            final String key = configurationKeys.next();
            log.info(key + " => " + getString(key));
        }
    }

    public Configuration<QwoBot> toPircBotXConfiguration(Listener<QwoBot> listener) {
        return new Configuration.Builder<QwoBot>()
                .setServerHostname(getString("server.host"))
                .setServerPort(getInt("server.port"))
                .setName(getString("bot.nick"))
                .setLogin(getString("bot.nick"))
                .setAutoReconnect(getBoolean("options.auto-reconnect"))
                .setAutoSplitMessage(true)
                .setShutdownHookEnabled(true)
                .addListener(listener)
                .addAutoJoinChannel(getString("server.channel.name"))
                .setMessageDelay(getLong("options.message-delay"))
                .buildConfiguration();
    }
}
