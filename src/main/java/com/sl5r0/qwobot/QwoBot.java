package com.sl5r0.qwobot;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.sl5r0.qwobot.core.ShutdownNotifier;
import com.sl5r0.qwobot.guice.QwoBotModule;
import com.sl5r0.qwobot.irc.IrcBotService;
import com.sl5r0.qwobot.irc.IrcServiceManager;
import com.sl5r0.qwobot.irc.service.bitcoin.BitCoinService;
import com.sl5r0.qwobot.irc.service.help.HelpService;
import com.sl5r0.qwobot.irc.service.logging.LoggingService;
import com.sl5r0.qwobot.irc.service.twitter.TwitterService;
import com.sl5r0.qwobot.irc.service.urls.UrlScanningService;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

public class QwoBot {
    private static final Logger log = getLogger(QwoBot.class);

    @Inject
    private QwoBot(ShutdownNotifier shutdownNotifier, IrcServiceManager serviceManager) {
        // Guice is using this in main()
        checkNotNull(shutdownNotifier, "shutdownNotifier must not be null");
        checkNotNull(serviceManager, "serviceManager must not be null");

        serviceManager
                .registerService(IrcBotService.class)
                .registerService(LoggingService.class)
                .registerService(UrlScanningService.class)
                .registerService(BitCoinService.class)
                .registerService(TwitterService.class)
                .registerService(HelpService.class);

        serviceManager.startAllUnstartedServices();
        shutdownNotifier.awaitShutdown();
    }

    public static void main(String[] args) throws Exception {
        final Injector injector = Guice.createInjector(new QwoBotModule());
        injector.getInstance(QwoBot.class);
    }
}
