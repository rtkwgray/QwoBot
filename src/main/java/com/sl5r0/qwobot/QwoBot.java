package com.sl5r0.qwobot;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.sl5r0.qwobot.core.ShutdownNotifier;
import com.sl5r0.qwobot.irc.service.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class QwoBot {
    @Inject
    private QwoBot(ShutdownNotifier shutdownNotifier,
                   IrcBotService ircBotService,
                   AccountManagementService ams,
                   QbuxService qbuxService,
                   LoggingService loggingService,
                   UrlScanningService urlScanningService) {

        checkNotNull(shutdownNotifier, "shutdownNotifier must not be null");
        ams.startAsync();
        ircBotService.startAsync();
        qbuxService.startAsync();
        loggingService.startAsync();
        urlScanningService.startAsync();
        shutdownNotifier.awaitShutdown();
    }

    public static void main(String[] args) throws Exception {
        Guice.createInjector(new QwobotModule()).getInstance(QwoBot.class);
    }
}