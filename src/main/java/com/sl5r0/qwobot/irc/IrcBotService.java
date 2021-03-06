package com.sl5r0.qwobot.irc;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.slf4j.Logger;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static org.pircbotx.PircBotX.State.CONNECTED;
import static org.pircbotx.PircBotX.State.INIT;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class IrcBotService extends AbstractExecutionThreadService {
    private static final Logger log = getLogger(IrcBotService.class);

    private final QwoBot bot;

    @Inject
    public IrcBotService(Configuration<PircBotX> configuration) {
        this.bot = new QwoBot(configuration);
    }

    @Override
    protected void run() throws Exception {
        while (isRunning()){
            try {
                log.info("Connecting to IRC");
                bot.startBot();
            } catch (Exception e) {
                log.warn("IRC bot was disconnected", e);
            }

            log.info("Waiting 5 seconds to reconnect");
            try {
                sleep(5000);
            } catch (InterruptedException ignored) {
                currentThread().interrupt();
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

    public PircBotX getBot() {
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
