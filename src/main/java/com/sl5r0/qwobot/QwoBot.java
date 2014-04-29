package com.sl5r0.qwobot;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.sl5r0.qwobot.core.ShutdownNotifier;
import com.sl5r0.qwobot.guice.QwoBotModule;
import com.sl5r0.qwobot.irc.service.*;
import com.sl5r0.qwobot.irc.service.account.AccountService;
import com.sl5r0.qwobot.irc.service.qbux.QbuxDistributionService;
import com.sl5r0.qwobot.irc.service.qbux.QbuxService;
import com.sl5r0.qwobot.irc.service.twitter.TwitterService;
import com.sl5r0.qwobot.security.AccountManager;
import com.sl5r0.qwobot.security.QwoBotRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.guice.ShiroModule;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.mgt.*;
import org.apache.shiro.mgt.SecurityManager;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

public class QwoBot {
    private static final Logger log = getLogger(QwoBot.class);

    // Guice is using this in main()
    @Inject
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
                .registerService(QbuxService.class)
                .registerService(AdminService.class)
                .registerService(QbuxDistributionService.class);

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

        setUpSecurityManager(injector);
        injector.getInstance(QwoBot.class);
    }

    private static void setUpSecurityManager(Injector injector) {
        final SecurityManager securityManager = injector.getInstance(SecurityManager.class);
        if (securityManager instanceof DefaultSecurityManager) {
            final SubjectDAO subjectDAO = ((DefaultSecurityManager) securityManager).getSubjectDAO();
            if (subjectDAO instanceof DefaultSubjectDAO) {
                final SessionStorageEvaluator sessionStorageEvaluator = ((DefaultSubjectDAO) subjectDAO).getSessionStorageEvaluator();
                if (sessionStorageEvaluator instanceof DefaultSessionStorageEvaluator) {
                    ((DefaultSessionStorageEvaluator) sessionStorageEvaluator).setSessionStorageEnabled(false);
                    SecurityUtils.setSecurityManager(securityManager);
                    log.debug("Disabled Shiro sessions successfully.");
                }
            }
        }
    }
}