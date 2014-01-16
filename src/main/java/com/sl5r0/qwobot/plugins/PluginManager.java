package com.sl5r0.qwobot.plugins;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sl5r0.qwobot.core.BotConfiguration;
import com.sl5r0.qwobot.core.QwoBot;
import com.sl5r0.qwobot.plugins.bitcoin.BitCoinPriceChecker;
import com.sl5r0.qwobot.plugins.commands.Command;
import com.sl5r0.qwobot.plugins.reddit.Reddit;
import com.sl5r0.qwobot.plugins.twitter.TwitterFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class PluginManager {
    private static final Set<Plugin> registeredPlugins = newHashSet();
    private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
    private final EventBus eventBus;
    private final Provider<QwoBot> botProvider;
    private final BotConfiguration configuration;

    @Inject
    public PluginManager(BotConfiguration configuration, Provider<QwoBot> botProvider, EventBus eventBus) {
        this.botProvider = botProvider;
        this.configuration = configuration;
        this.eventBus = eventBus;
    }

    public void initializePlugins() {
        final QwoBot bot = botProvider.get();

        // TODO: don't hard-code this.
        try {
            registerPlugin(new Reddit(configuration));
        } catch (RuntimeException e) {
            log.error("Couldn't load Reddit plugin", e);
        }

        try {
            registerPlugin(new BitCoinPriceChecker());
        } catch (RuntimeException e) {
            log.error("Couldn't load BitCoin price checker", e);
        }

        try {
            registerPlugin(new TwitterFeed(configuration, bot));
        } catch (RuntimeException e) {
            log.error("Couldn't load Twitter plugin", e);
        }
    }

    public void registerPlugin(Plugin plugin) {
        if (registeredPlugins.contains(plugin)) {
            log.warn(plugin.getName() + " plugin was already loaded.");
            return;
        }

        log.info("Loading plugin: " + plugin);
        registeredPlugins.add(plugin);
        for (Command command : plugin.getCommands()) {
            eventBus.register(command);
            log.debug("Registered command: " + command.getHelp());
        }
    }
}
