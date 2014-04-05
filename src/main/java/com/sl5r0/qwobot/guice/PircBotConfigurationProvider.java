package com.sl5r0.qwobot.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sl5r0.qwobot.core.IrcEventDispatcher;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

import static com.google.common.base.Preconditions.checkNotNull;

public class PircBotConfigurationProvider implements Provider<Configuration<PircBotX>> {
    private final Provider<org.apache.commons.configuration.Configuration> qwoBotConfiguration;
    private final IrcEventDispatcher eventDispatcher;

    @Inject
    public PircBotConfigurationProvider(Provider<org.apache.commons.configuration.Configuration> qwoBotConfiguration, IrcEventDispatcher eventDispatcher) {
        this.qwoBotConfiguration = checkNotNull(qwoBotConfiguration, "qwoBotConfiguration must not be null");
        this.eventDispatcher = checkNotNull(eventDispatcher, "eventDispatcher must not be null");
    }

    @Override
    public Configuration<PircBotX> get() {
        final org.apache.commons.configuration.Configuration config = qwoBotConfiguration.get();
        return new Configuration.Builder<>()
                .setServerHostname(config.getString("server.host"))
                .setServerPort(config.getInt("server.port"))
                .setName(config.getString("bot.nick"))
                .setLogin(config.getString("bot.nick"))
                .setAutoReconnect(config.getBoolean("options.auto-reconnect"))
                .setAutoSplitMessage(true)
                .setShutdownHookEnabled(true)
                .addListener(eventDispatcher)
                .addAutoJoinChannel(config.getString("server.channel.name"))
                .setMessageDelay(config.getLong("options.message-delay"))
                .buildConfiguration();
    }
}
