package com.sl5r0.qwobot.irc.service.qbux;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.Account;
import com.sl5r0.qwobot.irc.service.IrcBotService;
import com.sl5r0.qwobot.persistence.AccountRepository;
import com.sl5r0.qwobot.security.AccountManager;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.AbstractScheduledService.Scheduler.newFixedRateSchedule;
import static com.sl5r0.qwobot.core.IrcTextFormatter.GREEN;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.joda.time.DateTime.now;
import static org.joda.time.Duration.standardHours;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class QbuxDistributionService extends AbstractScheduledService {
    private static final Logger log = getLogger(QbuxDistributionService.class);
    private final AccountRepository accountRepository;
    private final AccountManager accountManager;
    private final PircBotX bot;

    private static final int BALANCE_INCREASE = 1;

    @Inject
    public QbuxDistributionService(IrcBotService ircBotService, AccountRepository accountRepository, AccountManager accountManager) {
        this.accountRepository = checkNotNull(accountRepository, "accountRepository must not be null");
        this.accountManager = checkNotNull(accountManager, "accountManager must not be null");
        this.bot = checkNotNull(ircBotService, "ircBotService must not be null").getBot();
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

            log.info("Gave " + BALANCE_INCREASE + " QBUX to all verified users");
            for (Channel channel : bot.getUserBot().getChannels()) {
                channel.send().message(GREEN.format("All logged in users just got " + BALANCE_INCREASE + " QBUX!"));
            }
        } catch (Throwable e) {
            log.error("QBUX distribution failed", e);
        }
    }

    @Override
    protected Scheduler scheduler() {
        // Schedule the next distribution time to be on the hour and every hour after that.
        final DateTime nextHour = now().hourOfDay().roundCeilingCopy();
        final long secondsUntilNextHour = new Duration(now(), nextHour).getStandardSeconds();
        log.info("Next QBUX distribution will be at " + nextHour + " (in " + secondsUntilNextHour + " seconds)");
        return newFixedRateSchedule(secondsUntilNextHour, standardHours(1).getStandardSeconds(), SECONDS);
    }
}
