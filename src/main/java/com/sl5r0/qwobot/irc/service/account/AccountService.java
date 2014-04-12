package com.sl5r0.qwobot.irc.service.account;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.core.AccountCredentials;
import com.sl5r0.qwobot.domain.Account;
import com.sl5r0.qwobot.irc.service.AbstractIrcEventService;
import com.sl5r0.qwobot.security.AccountManager;
import com.sl5r0.qwobot.security.exceptions.AccountHasPasswordException;
import com.sl5r0.qwobot.security.exceptions.BotCannotSeeUserException;
import com.sl5r0.qwobot.security.exceptions.IncorrectPasswordException;
import com.sl5r0.qwobot.security.exceptions.UsernameAlreadyExistsException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.UserListEvent;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sl5r0.qwobot.core.IrcTextFormatter.*;
import static com.sl5r0.qwobot.security.AccountManager.getActingAccount;

@Singleton
public class AccountService extends AbstractIrcEventService {
    private final AccountManager accountManager;

    @Inject
    public AccountService(EventBus eventBus, AccountManager accountManager) {
        super(eventBus);
        this.accountManager = checkNotNull(accountManager, "accountManager must not be null");
    }

    @Subscribe
    public void handleJoins(JoinEvent<PircBotX> event) {
        greet(event.getUser());
    }

    @Subscribe
    public void login(PrivateMessageEvent<PircBotX> event) {
        final List<String> arguments = argumentsFor("!account:login", event.getMessage(), 2);

        try {
            accountManager.login(event.getUser(), new AccountCredentials(arguments.get(0), arguments.get(1)));
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

    @Subscribe
    public void whoAmI(PrivateMessageEvent<PircBotX> event) {
        argumentsFor("!account:whoami", event.getMessage(), 0);
        final Account account = getActingAccount();
        event.respond("You're currently logged in as " + CYAN.format(account.getUsername()) + ".");
    }

    @Subscribe
    public void password(PrivateMessageEvent<PircBotX> event) {
        final List<String> arguments = argumentsFor("!account:password", event.getMessage(), 0);
        if (arguments.size() >= 1) {
            try {
                final String password = arguments.get(0);
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
        } else {
            showPasswordStatus(event);
        }
    }

    @RequiresAuthentication
    protected void showPasswordStatus(PrivateMessageEvent<PircBotX> event) {
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
}
