package com.sl5r0.qwobot.irc.service.qbux;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.Account;
import com.sl5r0.qwobot.irc.service.IrcBotService;
import com.sl5r0.qwobot.persistence.AccountRepository;
import com.sl5r0.qwobot.security.AccountManager;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.AbstractScheduledService.Scheduler.newFixedRateSchedule;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class QbuxDistributionService extends AbstractScheduledService {
    private static final Logger log = getLogger(QbuxDistributionService.class);
    private final AccountRepository accountRepository;
    private final AccountManager accountManager;

    private static final int BALANCE_INCREASE = 1;

    @Inject
    public QbuxDistributionService(IrcBotService ircBotService, AccountRepository accountRepository, EventBus eventBus, AccountManager accountManager) {
        this.accountRepository = checkNotNull(accountRepository, "accountRepository must not be null");
        this.accountManager = checkNotNull(accountManager, "accountManager must not be null");
    }

    @Override
    protected void runOneIteration() throws Exception {
        try {
            for (Long userId : accountManager.getAuthenticatedUserIds()) {
                final Optional<Account> qwobotUser = accountRepository.findById(userId);
                if (qwobotUser.isPresent()) {
                    qwobotUser.get().modifyBalance(BALANCE_INCREASE);
                    accountRepository.saveOrUpdate(qwobotUser.get());
                }
            }
        } catch (Throwable e) {
            log.error("Something went wrong :( ", e);
        }
    }

    @Override
    protected Scheduler scheduler() {
        return newFixedRateSchedule(1, 60, MINUTES);
    }
}
