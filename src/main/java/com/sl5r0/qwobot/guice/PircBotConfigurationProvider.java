package com.sl5r0.qwobot.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sl5r0.qwobot.irc.IrcEventDispatcher;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

import static com.google.common.base.Preconditions.checkNotNull;

public class PircBotConfigurationProvider implements Provider<Configuration<PircBotX>> {
    private final HierarchicalConfiguration qwoBotConfiguration;
    private final IrcEventDispatcher eventDispatcher;

    @Inject
    public PircBotConfigurationProvider(HierarchicalConfiguration qwoBotConfiguration, IrcEventDispatcher eventDispatcher) {
        this.qwoBotConfiguration = checkNotNull(qwoBotConfiguration, "qwoBotConfiguration must not be null");
        this.eventDispatcher = checkNotNull(eventDispatcher, "eventDispatcher must not be null");
    }

    @Override
    public Configuration<PircBotX> get() {
        final org.apache.commons.configuration.Configuration config = qwoBotConfiguration;
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
