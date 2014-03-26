package com.sl5r0.qwobot.plugins.qbux.bankmanager;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.sl5r0.qwobot.persistence.QwobotUserRepository;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.pircbotx.Channel;
import org.pircbotx.User;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.util.concurrent.AbstractScheduledService.Scheduler.newFixedRateSchedule;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.joda.time.DateTime.now;

public class BankManager extends AbstractScheduledService {
    private final Set<Channel> channels;
    private final QwobotUserRepository repository;

    public BankManager(QwobotUserRepository repository, Set<Channel> channels) {
        this.repository = checkNotNull(repository);
        this.channels = copyOf(checkNotNull(channels));
    }

    @Override
    protected void runOneIteration() throws Exception {
        for (Channel channel : channels) {
            for (User user : channel.getUsers()) {
//                repository.modifyBalance(user, 1);
            }
        }
    }

    @Override
    protected Scheduler scheduler() {
        DateTime firstRun = now().plusHours(1).withMinuteOfHour(0).withSecondOfMinute(0);
        Period initialDelay = new Period(now(), firstRun);
        return newFixedRateSchedule(initialDelay.getSeconds(), 3600, SECONDS);
    }
}
