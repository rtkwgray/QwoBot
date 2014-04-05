package com.sl5r0.qwobot.irc.service;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.pircbotx.PircBotX.State.CONNECTED;
import static org.pircbotx.PircBotX.State.INIT;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class IrcBotService extends AbstractExecutionThreadService implements Provider<PircBotX> {
    private static final Logger log = getLogger(IrcBotService.class);
    private final Provider<Configuration<PircBotX>> configurationProvider;
    private QwoBot bot;

    @Inject
    public IrcBotService(Provider<Configuration<PircBotX>> configurationProvider) {
        this.configurationProvider = checkNotNull(configurationProvider, "configurationProvider must not be null");
        bot = new QwoBot(configurationProvider.get());
    }

    @Override
    protected void run() throws Exception {
        while (isRunning()){
            try {
                bot.startBot();
            } catch (Exception e) {
                log.warn("Bot disconnected.", e);
            }
        }
    }

    @Override
    protected void startUp() throws Exception {
        log.info("Starting service.");
    }

    @Override
    protected void triggerShutdown() {
        log.info("Stopping service.");
        if (bot.getState() == INIT || bot.getState() == CONNECTED) {
            bot.shutdown();
        }
    }

    @Override
    public PircBotX get() {
        if (bot == null) {
            throw new IllegalStateException("No bot has been created. Is IrcBotService running?");
        }

        return bot;
    }

    /**
     * For some reason, shutdown() is protected, so we need to extend PircBotX.
     */
    private class QwoBot extends PircBotX {
        private QwoBot(Configuration<? extends PircBotX> configuration) {
            super(configuration);
        }

        @Override
        protected void shutdown() {
            super.shutdown(true);
        }
    }
}
