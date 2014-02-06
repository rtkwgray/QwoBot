package com.sl5r0.qwobot.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.pircbotx.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sl5r0.qwobot.core.QwoBotModule.BotConfigFile;

@Singleton
public class BotConfiguration extends XMLConfiguration {
    private static final Logger log = LoggerFactory.getLogger(BotConfiguration.class);
    private final QwoBotListener eventListener;

    @Inject
    public BotConfiguration(@BotConfigFile File config, QwoBotListener eventListener) throws ConfigurationException {
        super(checkNotNull(config, "config cannot be null"));
        this.eventListener = checkNotNull(eventListener, "eventListener cannot be null");

        log.info("Loaded configuration from " + config.getAbsolutePath());
        final Iterator<String> configurationKeys = getKeys();
        while (configurationKeys.hasNext()) {
            final String key = configurationKeys.next();
            log.info(key + " => " + getString(key));
        }
    }

    public Configuration<QwoBot> toPircBotXConfiguration() {
        return new Configuration.Builder<QwoBot>()
                .setServerHostname(getString("server.host"))
                .setServerPort(getInt("server.port"))
                .setName(getString("bot.nick"))
                .setLogin(getString("bot.nick"))
                .setAutoReconnect(getBoolean("options.auto-reconnect"))
                .setAutoSplitMessage(true)
                .setShutdownHookEnabled(true)
                .addListener(eventListener)
                .addAutoJoinChannel(getString("server.channel.name"))
                .setMessageDelay(getLong("options.message-delay"))
                .buildConfiguration();
    }
}
