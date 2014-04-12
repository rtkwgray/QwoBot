package com.sl5r0.qwobot;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.sl5r0.qwobot.core.ShutdownNotifier;
import com.sl5r0.qwobot.irc.service.account.AccountService;
import com.sl5r0.qwobot.security.AccountManager;
import com.sl5r0.qwobot.guice.QwoBotModule;
import com.sl5r0.qwobot.irc.service.*;
import com.sl5r0.qwobot.irc.service.twitter.TwitterService;
import com.sl5r0.qwobot.security.QwoBotRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.guice.ShiroModule;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.mgt.SecurityManager;

import static com.google.common.base.Preconditions.checkNotNull;

public class QwoBot {
    @Inject
    @SuppressWarnings("UnusedDeclaration") // Guice is using this in main()
    private QwoBot(ShutdownNotifier shutdownNotifier, IrcServiceManager serviceManager) {
        checkNotNull(shutdownNotifier, "shutdownNotifier must not be null");
        checkNotNull(serviceManager, "serviceManager must not be null");

        serviceManager
                .registerService(IrcBotService.class)
                .registerService(LoggingService.class)
                .registerService(UrlScanningService.class)
                .registerService(BitCoinService.class)
                .registerService(TwitterService.class)
                .registerService(ManagementService.class)
                .registerService(AccountService.class)
                .registerService(HelpService.class)
                .registerService(QbuxService.class);

        serviceManager.startAllUnstartedServices();
        shutdownNotifier.awaitShutdown();
    }

    public static void main(String[] args) throws Exception {
        final Injector injector = Guice.createInjector(
                new ShiroModule() {
                    @Override
                    protected void configureShiro() {
                        try {
                            bindRealm().toConstructor(QwoBotRealm.class.getConstructor(AccountManager.class));
                        } catch (NoSuchMethodException e) {
                            addError(e);
                        }
                    }
                },
                new ShiroAopModule(),
                new QwoBotModule()
        );
        SecurityUtils.setSecurityManager(injector.getInstance(SecurityManager.class));
        injector.getInstance(QwoBot.class);
    }
}