package com.sl5r0.qwobot.irc.service.qbux;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.sl5r0.qwobot.domain.Account;
import com.sl5r0.qwobot.domain.help.Command;
import com.sl5r0.qwobot.irc.service.AbstractIrcEventService;
import com.sl5r0.qwobot.persistence.AccountRepository;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static com.sl5r0.qwobot.security.AccountManager.getActingAccount;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static org.slf4j.LoggerFactory.getLogger;

public class QbuxService extends AbstractIrcEventService {
    private static final Logger log = getLogger(QbuxService.class);
    private static final Command showBalance = new Command("!qbux:balance", "Show balance for a user").addOptionalParameter("nickname");
    private static final Command tip = new Command("!qbux:tip", "Give QBUX to the specified user").addParameter("nickname").addParameter("amount").addParameter("reason");
    private static final Command richest = new Command("!qbux:richest", "Show a selection of users with the highest account balance").addOptionalParameter("nickname");

    private final AccountRepository accountRepository;

    @Inject
    protected QbuxService(AccountRepository accountRepository) {
        super(newHashSet(showBalance, tip, richest));
        this.accountRepository = checkNotNull(accountRepository);
    }

    @Subscribe
    public void showBalance(PrivateMessageEvent<PircBotX> event) {
        doShowBalance(event);
    }

    @Subscribe
    public void showBalance(MessageEvent<PircBotX> event) {
        doShowBalance(event);
    }

    private void doShowBalance(GenericMessageEvent<PircBotX> event) {
        final List<String> arguments = argumentsFor(showBalance, event.getMessage());
        if (arguments.isEmpty()) {
            showPersonalBalance(event);
        } else {
            showUserBalance(event, arguments.get(0));
        }
    }

    @RequiresAuthentication
    protected void showPersonalBalance(GenericMessageEvent<PircBotX> event) {
        event.respond("You have " + getActingAccount().getBalance() + " QBUX.");
    }

    private void showUserBalance(GenericMessageEvent<PircBotX> event, String nickname) {
        final Optional<Account> account = accountRepository.findByNick(nickname);
        if (account.isPresent()) {
            event.respond(account.get().getUsername() + " has " + account.get().getBalance() + " QBUX.");
        } else {
            event.respond("I don't have any record of that account.");
        }
    }

    @Subscribe
    public void tip(PrivateMessageEvent<PircBotX> event) {
        final List<String> arguments = argumentsFor(tip, event.getMessage());
        final int amount;
        try {
            amount = parseInt(arguments.get(1));
        } catch (NumberFormatException e) {
            event.respond("\"" + arguments.get(1) + "\" doesn't look like a valid number to me.");
            return;
        }

        doTip(event, arguments.get(0), amount, Joiner.on(" ").join(arguments.subList(2, arguments.size())));
    }

    @RequiresAuthentication
    protected void doTip(PrivateMessageEvent<PircBotX> event, String nickToTip, int amount, String reason) {
        try {
            checkArgument(amount > 0, "tip amount must be positive");
            checkArgument(!nickToTip.equals(getActingAccount().getUsername()), "you can't tip yourself");
            final Optional<Account> to = accountRepository.findByNick(nickToTip);
            if (to.isPresent()) {
                final Account giver = getActingAccount();
                final Account receiver = to.get();
                giver.modifyBalance(-amount);
                receiver.modifyBalance(amount);
                accountRepository.saveOrUpdate(giver);
                accountRepository.saveOrUpdate(receiver);
                final String message = receiver.getUsername() + " was tipped " + amount + " QBUX by " + giver.getUsername() + " " + reason;
                for (Channel channel : event.getBot().getUserChannelDao().getUser(receiver.getUsername()).getChannels()) {
                    channel.send().message(message);
                }
            } else {
                event.respond("I don't have any record of that user.");
            }
        } catch (IllegalArgumentException e) {
            event.respond(e.getMessage());
        }
    }

    @Subscribe
    public void showRichest(MessageEvent<PircBotX> event) {
        final List<String> arguments = argumentsFor(richest, event.getMessage());
        int numberToFind = 1;
        if (!arguments.isEmpty()) {
            try {
                numberToFind = max(abs(parseInt(arguments.get(1))), 10);
            } catch (NumberFormatException e) {
                log.info("Couldn't parse \"" + arguments.get(1) + "\" as a number");
            }
        }

        final List<Account> richest = accountRepository.findRichest(numberToFind);
        event.respond("Top " + numberToFind + " richest people:");
        event.respond(Joiner.on(", ").join(Lists.transform(richest, new Function<Account, String>() {
            @Override
            public String apply(Account input) {
                return input.getUsername() + " (" + input.getBalance() + " QBUX)";
            }
        })));
    }
}
