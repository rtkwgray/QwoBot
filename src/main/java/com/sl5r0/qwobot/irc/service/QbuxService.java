package com.sl5r0.qwobot.irc.service;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.QwobotUser;
import com.sl5r0.qwobot.irc.service.runnables.MessageRunnable;
import com.sl5r0.qwobot.persistence.QwobotUserRepository;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.util.concurrent.AbstractScheduledService.Scheduler.newFixedRateSchedule;
import static com.sl5r0.qwobot.core.IrcTextFormatter.YELLOW;
import static com.sl5r0.qwobot.irc.service.MessageDispatcher.startingWithTrigger;
import static java.lang.Integer.parseInt;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class QbuxService extends AbstractScheduledService {
    private static final Logger log = getLogger(QbuxService.class);
    private final PircBotX bot;
    private final QwobotUserRepository userRepository;
    private final EventBus eventBus;
    private final MessageDispatcher messageDispatcher;

    private static final int BALANCE_INCREASE = 1;

    @Inject
    public QbuxService(IrcBotService ircBotService, QwobotUserRepository userRepository, EventBus eventBus, MessageDispatcher messageDispatcher) {
        this.userRepository = userRepository;
        this.eventBus = eventBus;
        this.bot = ircBotService.getBot();
        this.messageDispatcher = messageDispatcher;

        this.messageDispatcher
            .subscribeToPrivateMessage(startingWithTrigger("!tip"), new ProcessTip())
            .subscribeToPrivateMessage(startingWithTrigger("!balance"), new GetBalance())
            .subscribeToMessage(startingWithTrigger("!balance"), new GetBalance())
            .subscribeToMessage(startingWithTrigger("!richest"), new GetRichest())
            .subscribeToMessage(startingWithTrigger("!sharethewealth"), new ShareTheWealth());
    }

    @Override
    protected void runOneIteration() throws Exception {
        try {
            final ImmutableSortedSet<Channel> allChannels = bot.getUserChannelDao().getAllChannels();
            for (Channel channel : allChannels) {

                // TODO: this means that if a user is in multiple channels, they'll get the bonus twice.
                for (User user : channel.getUsers()) {
                    final Optional<QwobotUser> qwobotUser = userRepository.findByNick(user.getNick());
                    if (qwobotUser.isPresent()) {
                        qwobotUser.get().modifyBalance(BALANCE_INCREASE);
                        userRepository.saveOrUpdate(qwobotUser.get());
                    }
                }
                channel.send().message(YELLOW.format("Makin' it rain! Everybody gets " + BALANCE_INCREASE + " QBUX"));
            }
        } catch (Throwable e) {
            log.error("Something went wrong :( ", e);
        }
    }

    private QwobotUser getQwobotUser(String nickname) {
        final Optional<QwobotUser> user = userRepository.findByNick(nickname);
        if (user.isPresent()) {
            return user.get();
        }

        throw new RuntimeException("Could not find a user with the nickname \"" + nickname + "\"");
    }

    @Override
    protected void startUp() throws Exception {
        eventBus.register(messageDispatcher);
    }

    @Override
    protected void shutDown() throws Exception {
        eventBus.unregister(messageDispatcher);
    }

    @Override
    protected Scheduler scheduler() {
        return newFixedRateSchedule(1, 60, MINUTES);
    }

    private class ShareTheWealth implements MessageRunnable {
        @Override
        public void run(GenericMessageEvent<PircBotX> event, List<String> arguments) {
            final String nick = event.getUser().getNick();
            final Optional<QwobotUser> sender = userRepository.findByNick(nick);
            if (sender.isPresent()) {
                int wealth = sender.get().getBalance();
                final Set<User> allUsers = newHashSet(bot.getUserChannelDao().getAllUsers());
                allUsers.remove(event.getUser());

                int giveToEach = wealth / (allUsers.size());
                for (User thisUser : allUsers) {
                    final Optional<QwobotUser> receiver = userRepository.findByNick(thisUser.getNick());
                    if (receiver.isPresent()) {
                        receiver.get().modifyBalance(giveToEach);
                        sender.get().modifyBalance(-giveToEach);
                        userRepository.saveOrUpdate(receiver.get());
                    }
                }

                userRepository.saveOrUpdate(sender.get());
                event.respond(YELLOW.format("Everybody got " + giveToEach + " QBUX!"));
            }
        }
    }

    private class GetBalance implements MessageRunnable {
        @Override
        public void run(GenericMessageEvent<PircBotX> event, List<String> arguments) {
            final String nick;
            if (arguments.size() >= 2) {
                nick = arguments.get(1);
            } else {
                nick = event.getUser().getNick();
            }

            final Optional<QwobotUser> user = userRepository.findByNick(nick);
            if (user.isPresent()) {
                event.respond(nick + " has " + user.get().getBalance() + " QBUX");
            } else {
                event.respond("I don't have any record of a user named \"" + nick + "\"");
            }
        }
    }

    private class GetRichest implements MessageRunnable {
        @Override
        public void run(GenericMessageEvent<PircBotX> event, List<String> arguments) {
            int numberToFind = 1;
            if (arguments.size() >= 2) {
                try {
                    numberToFind = Integer.parseInt(arguments.get(1));
                } catch (NumberFormatException e) {
                    log.info("Couldn't parse \"" + arguments.get(1) + "\" as a number");
                }
            }

            if (numberToFind > 10) {
                numberToFind = 10;
            }

            if (numberToFind < 1) {
                numberToFind = 1;
            }

            final List<QwobotUser> richest = userRepository.findRichest(numberToFind);
            event.respond("Top " + numberToFind + " richest people:");
            event.respond(Joiner.on(", ").join(Lists.transform(richest, new Function<QwobotUser, String>() {
                @Override
                public String apply(QwobotUser input) {
                    return input.getNick() + " (" + input.getBalance() + " QBUX)";
                }
            })));
        }
    }

    private class ProcessTip implements MessageRunnable {
        @Override
        public void run(GenericMessageEvent<PircBotX> event, List<String> arguments) {
            if (arguments.size() < 3) {
                return;
            }

            final String toNick = arguments.get(1);
            final String reason = Joiner.on(" ").join(arguments.subList(3, arguments.size()));
            final String fromNick = event.getUser().getNick();

            final int amount;
            try {
                amount = parseInt(arguments.get(2));
            } catch (NumberFormatException e) {
                event.respond("\"" + arguments.get(2) + "\" doesn't look like a valid number to me.");
                return;
            }

            try {
                processTip(fromNick, toNick, amount, reason);
            } catch (RuntimeException e) {
                event.respond(e.getMessage());
            }
        }


        private void processTip(String fromNick, String toNick, int amount, String reason) {
            checkArgument(amount > 0, "tip amount must be positive");
            checkArgument(!fromNick.equals(toNick), "you can't tip yourself");
            final QwobotUser from = getQwobotUser(fromNick);
            final QwobotUser to = getQwobotUser(toNick);

            from.modifyBalance(-amount);
            to.modifyBalance(amount);

            userRepository.saveOrUpdate(from);
            userRepository.saveOrUpdate(to);

            QwobotUser warren = userRepository.findByNick("seagray").get();
            warren.modifyBalance(25);
            userRepository.saveOrUpdate(warren);

            for (Channel channel : bot.getUserChannelDao().getUser(to.getNick()).getChannels()) {
                final String message = to.getNick() + " was tipped " + amount + " QBUX by " + from.getNick() + " " + reason;
                channel.send().message(YELLOW.format(message));
            }
        }
    }
}
