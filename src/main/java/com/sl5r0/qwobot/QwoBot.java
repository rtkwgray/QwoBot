package com.sl5r0.qwobot;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.sl5r0.qwobot.core.ShutdownNotifier;
import com.sl5r0.qwobot.guice.QwoBotModule;
import com.sl5r0.qwobot.irc.service.*;
import com.sl5r0.qwobot.irc.service.twitter.TwitterService;

import static com.google.common.base.Preconditions.checkNotNull;

public class QwoBot {
    @Inject
    @SuppressWarnings("UnusedDeclaration") // Guice is using this in main()
    private QwoBot(ShutdownNotifier shutdownNotifier, IrcServiceManager serviceManager) {
        checkNotNull(shutdownNotifier, "shutdownNotifier must not be null");
        checkNotNull(serviceManager, "serviceManager must not be null");

        serviceManager
                .registerService(IrcBotService.class)
                .registerService(AccountManagementService.class)
                .registerService(LoggingService.class)
                .registerService(UrlScanningService.class)
                .registerService(BitCoinService.class)
                .registerService(TwitterService.class)
                .registerService(ManagementService.class)
                .registerService(QbuxService.class);

        serviceManager.startAllUnstartedServices();
        shutdownNotifier.awaitShutdown();
    }

    public static void main(String[] args) throws Exception {
        Guice.createInjector(new QwoBotModule()).getInstance(QwoBot.class);
    }
}