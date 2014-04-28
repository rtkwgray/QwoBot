package com.sl5r0.qwobot.irc.service.account;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.core.AccountCredentials;
import com.sl5r0.qwobot.domain.Account;
import com.sl5r0.qwobot.domain.command.CommandHandler;
import com.sl5r0.qwobot.irc.service.AbstractIrcEventService;
import com.sl5r0.qwobot.security.AccountManager;
import com.sl5r0.qwobot.security.exceptions.AccountHasPasswordException;
import com.sl5r0.qwobot.security.exceptions.BotCannotSeeUserException;
import com.sl5r0.qwobot.security.exceptions.IncorrectPasswordException;
import com.sl5r0.qwobot.security.exceptions.UsernameAlreadyExistsException;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.*;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sl5r0.qwobot.core.IrcTextFormatter.*;
import static com.sl5r0.qwobot.domain.command.Command.forEvent;
import static com.sl5r0.qwobot.domain.command.Parameter.*;
import static com.sl5r0.qwobot.security.AccountManager.getActingAccount;

@Singleton
public class AccountService extends AbstractIrcEventService {
    private final AccountManager accountManager;

    @Inject
    public AccountService(AccountManager accountManager) {
        this.accountManager = checkNotNull(accountManager, "accountManager must not be null");
    }

    @Subscribe
    public void handleJoins(JoinEvent<PircBotX> event) {
        greet(event.getUser());
    }

    @Subscribe
    public void handleParts(PartEvent<PircBotX> event) {
        accountManager.logOutUserIfNotVisible(event.getUser());
    }

    @Subscribe
    public void handleQuits(QuitEvent<PircBotX> event) {
        accountManager.logOutUserIfNotVisible(event.getUser());
    }

    @Subscribe
    public void handleKicks(KickEvent<PircBotX> event) {
        accountManager.logOutUserIfNotVisible(event.getUser());
    }

    @Subscribe
    public void handleDisconnects(DisconnectEvent<PircBotX> event) {
        accountManager.logOutAllUsers();
    }

    public void login(PrivateMessageEvent event, String username, String password) {
        try {
            accountManager.login(event.getUser(), new AccountCredentials(username, password));
            event.respond("Success! You're logged in, buddy!");
        } catch (IncorrectPasswordException e) {
            event.respond("This is not the login you are looking for....");
        } catch (BotCannotSeeUserException e) {
            event.respond("It doesn't look like you're in any of my channels.");
        }

    }

    @Subscribe
    public void logInChannelUsers(UserListEvent<PircBotX> event) {
        for (User user : event.getUsers()) {
            if (!user.equals(event.getBot().getUserBot())) {
                greet(user);
            }
        }
    }

    protected void whoAmI(PrivateMessageEvent event) {
        event.respond("You're currently logged in as " + CYAN.format(getActingAccount().getUsername()) + ".");
    }

    private void password(PrivateMessageEvent event, String password) {
        try {
            accountManager.setAccountPassword(password);
            final Account account = getActingAccount();
            if (account.getPassword().isPresent()) {
                event.respond("Account password has been changed to " + CYAN.format(password));
            } else {
                event.respond("Account password has been removed.");
            }
        } catch (IncorrectPasswordException e) {
            event.respond("Sorry, your password doesn't match.");
        }
    }

    private void showPasswordStatus(PrivateMessageEvent event) {
        final Account account = getActingAccount();
        final String passwordStatus;
        if (account.getPassword().isPresent()) {
            passwordStatus = GREEN.format("a password");
        } else {
            passwordStatus = RED.format("no password");
        }

        event.respond("Your account currently has " + passwordStatus + " set.");
    }

    private void greet(User user) {
        if (user.equals(user.getBot().getUserBot())) {
            log.trace("Ignoring request to greet the bot user");
            return;
        }

        try {
            accountManager.login(user);
            user.send().message("Welcome back, " + user.getNick() + "!");
        } catch (IncorrectPasswordException e) {
            createNewAccount(user);
        } catch (AccountHasPasswordException e) {
            // TODO: if a user logs in, leaves, and then immediately rejoins, they're still authenticated....
            user.send().message("The account associated with this nickname has set a password. Some commands have been disabled until you log in.");
        }
    }

    private void createNewAccount(User user) {
        try {
            accountManager.createAccount(user.getNick());
            accountManager.login(user);
            user.send().message("Hello, " + CYAN.format(user.getNick()) + ". I've created a new account for you.");
        } catch (UsernameAlreadyExistsException e) {
            user.send().message("I tried to create an account for you, but on has already been created with the username " + CYAN.format(user.getNick()) + ".");
        } catch (AccountHasPasswordException | IncorrectPasswordException e) {
            log.warn("Tried to log in to account \"" + user.getNick() + "\" but failed", e);
            user.send().message("Hello, " + user.getNick() + ". You've got a new account with username " + CYAN.format(user.getNick()) + ", but I couldn't log you in.");
        }
    }

    @Override
    protected void initialize() {
        registerCommand(forEvent(PrivateMessageEvent.class)
                .addParameters(literal("!account:login"), string("username"), string("password"))
                .description("Log into an account")
                .handler(new CommandHandler<PrivateMessageEvent>() {
                    public void handle(PrivateMessageEvent event, List<String> arguments) {
                        login(event, arguments.get(1), arguments.get(2));
                    }
                })
                .build()
        );

        registerCommand(forEvent(PrivateMessageEvent.class)
                        .addParameters(literal("!account:whoami"))
                        .description("Show the current user")
                        .handler(new CommandHandler<PrivateMessageEvent>() {
                            public void handle(PrivateMessageEvent event, List<String> arguments) {
                                whoAmI(event);
                            }
                        })
                        .build()
        );

        registerCommand(forEvent(PrivateMessageEvent.class)
                        .addParameters(literal("!account:password"), optional(string("password")))
                        .description("Remove the account password if it matches the provided password, or set the account password if one doesn't exist.")
                        .handler(new CommandHandler<PrivateMessageEvent>() {
                            public void handle(PrivateMessageEvent event, List<String> arguments) {
                                if (arguments.size() > 1) {
                                    password(event, arguments.get(1));
                                } else {
                                    showPasswordStatus(event);
                                }
                            }
                        })
                        .build()
        );
    }
}
