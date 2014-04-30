package com.sl5r0.qwobot.irc.service.qbux;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sl5r0.qwobot.domain.Account;
import com.sl5r0.qwobot.irc.command.CommandHandler;
import com.sl5r0.qwobot.irc.service.AbstractIrcEventService;
import com.sl5r0.qwobot.persistence.AccountRepository;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.sl5r0.qwobot.domain.Roles.OWNER;
import static com.sl5r0.qwobot.irc.command.Command.forEvent;
import static com.sl5r0.qwobot.irc.command.Parameter.*;
import static com.sl5r0.qwobot.security.AccountManager.getActingAccount;
import static java.lang.Integer.parseInt;
import static java.lang.Math.min;

public class QbuxService extends AbstractIrcEventService {
    private final AccountRepository accountRepository;

    @Inject
    protected QbuxService(AccountRepository accountRepository) {
        this.accountRepository = checkNotNull(accountRepository);
    }

    @Override
    protected void initialize() {
        registerCommand(
                forEvent(GenericMessageEvent.class)
                        .addParameters(literal("!qbux:balance"), optional(string("nickname")))
                        .description("Show balance for a user")
                        .handler(new CommandHandler<GenericMessageEvent>() {
                            @Override
                            public void handle(GenericMessageEvent event, List<String> arguments) {
                                if (arguments.size() > 1) {
                                    showUserBalance(event, arguments.get(1));
                                } else {
                                    showPersonalBalance(event);
                                }
                            }
                        })
                        .build()
        );

        registerCommand(
                forEvent(PrivateMessageEvent.class)
                        .addParameters(literal("!qbux:tip"), string("nickname"), integer("amount"))
                        .description("Tip a user")
                        .handler(new CommandHandler<PrivateMessageEvent>() {
                            @Override
                            public void handle(PrivateMessageEvent event, List<String> arguments) {
                                doTip(event, arguments.get(1), parseInt(arguments.get(2)));
                            }
                        })
                        .build()
        );

        registerCommand(
                forEvent(GenericMessageEvent.class)
                        .addParameters(literal("!qbux:richest"), optional(integer("number of users to show")))
                        .description("Show the richest users")
                        .handler(new CommandHandler<GenericMessageEvent>() {
                            @Override
                            public void handle(GenericMessageEvent event, List<String> arguments) {
                                if (arguments.size() > 1) {
                                    showRichest(event, min(parseInt(arguments.get(1)), 10));
                                } else {
                                    showRichest(event, 3);
                                }
                            }
                        })
                        .build()
        );

        registerCommand(
                forEvent(GenericMessageEvent.class)
                        .addParameters(literal("!qbux:setbalance"), string("account name"), integer("amount"))
                        .description("Set a user's balance")
                        .handler(new CommandHandler<GenericMessageEvent>() {
                            @Override
                            public void handle(GenericMessageEvent event, List<String> arguments) {
                                setBalance(event, arguments.get(1), parseInt(arguments.get(2)));
                            }
                        })
                        .build()
        );
    }

    private void showPersonalBalance(GenericMessageEvent event) {
        event.respond("You have " + getActingAccount().getBalance() + " QBUX.");
    }

    @RequiresRoles(OWNER)
    protected void setBalance(GenericMessageEvent event, String accountName, int balance) {
        final Optional<Account> account = accountRepository.findByNick(accountName);
        if (account.isPresent()) {
            account.get().setBalance(balance);
            accountRepository.saveOrUpdate(account.get());
            event.respond(accountName + " now has a balance of " + balance + " QBUX");
            event.getBot().getUserChannelDao().getUser(accountName).send().message("Hey, " + getActingAccount().getUsername() + " just set your account balance to " + balance + " QBUX");
        } else {
            event.respond("No user exists with that username");
        }
    }

    private void showUserBalance(GenericMessageEvent event, String nickname) {
        final Optional<Account> account = accountRepository.findByNick(nickname);
        if (account.isPresent()) {
            event.respond(account.get().getUsername() + " has " + account.get().getBalance() + " QBUX.");
        } else {
            event.respond("I don't have any record of that account.");
        }
    }

    private void doTip(PrivateMessageEvent event, String nickToTip, int amount) {
        try {
            checkArgument(amount > 0, "tip amount must be positive");
            checkArgument(!nickToTip.equals(getActingAccount().getUsername()), "you can't tip yourself");

            final User tippedUser = event.getBot().getUserChannelDao().getUser(nickToTip);
            final String tippedHostString = tippedUser.getLogin() + "@" + tippedUser.getHostmask();
            final String tipperHostString = event.getUser().getLogin() + "@" + event.getUser().getHostmask();

            if (tippedHostString.equals(tipperHostString)) {
                throw new IllegalArgumentException("you can't tip yourself");
            }

            final Optional<Account> to = accountRepository.findByNick(nickToTip);
            if (to.isPresent()) {
                final Account giver = getActingAccount();
                final Account receiver = to.get();
                giver.modifyBalance(-amount);
                receiver.modifyBalance(amount);
                accountRepository.saveOrUpdate(giver);
                accountRepository.saveOrUpdate(receiver);
                final String message = nickToTip + " was tipped " + amount + " QBUX by " + giver.getUsername();
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

    public void showRichest(GenericMessageEvent event, int numberToFind) {
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
