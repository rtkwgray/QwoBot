package com.sl5r0.qwobot.irc.service.account;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.core.AccountCredentials;
import com.sl5r0.qwobot.core.AccountManager;
import com.sl5r0.qwobot.domain.Account;
import com.sl5r0.qwobot.irc.service.AbstractIrcEventService;
import com.sl5r0.qwobot.security.exceptions.AuthenticationFailedException;
import com.sl5r0.qwobot.security.exceptions.NoSuchVisibleUserException;
import com.sl5r0.qwobot.security.exceptions.SecuredAccountException;
import com.sl5r0.qwobot.security.exceptions.UsernameAlreadyExistsException;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.*;
import org.pircbotx.hooks.types.GenericUserEvent;

import java.util.List;

import static com.google.common.base.Optional.of;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.sl5r0.qwobot.core.IrcTextFormatter.format;

@Singleton
public class AccountSecurityService extends AbstractIrcEventService {
    private final AccountManager accountManager;

    @Inject
    public AccountSecurityService(EventBus eventBus, AccountManager accountManager) {
        super(eventBus);
        this.accountManager = checkNotNull(accountManager, "accountManager must not be null");
    }

    @Subscribe
    public void handleJoins(JoinEvent<PircBotX> event) {
        greet(event.getUser());
    }

    @Subscribe
    public void handleQuits(QuitEvent<PircBotX> event) {
        refreshVerification(event);
    }

    @Subscribe
    public void handleParts(PartEvent<PircBotX> event) {
        refreshVerification(event);
    }

    @Subscribe
    public void handleKicks(KickEvent<PircBotX> event) {
        refreshVerification(event);
    }

    @Subscribe
    public void accountLogin(PrivateMessageEvent<PircBotX> event) {
        final List<String> arguments = argumentsFor("!account:login", event.getMessage());
        if (arguments.size() < 3) {
            return;
        }

        try {
            accountManager.login(event.getUser().getNick(), of(new AccountCredentials(arguments.get(1), arguments.get(2))), true);
        } catch (SecuredAccountException ignored) {
        } catch (NoSuchVisibleUserException | AuthenticationFailedException e) {
            event.respond("This is not the login you are looking for....");
            return;
        }

        event.respond("Success! You're logged in, buddy!");
    }

    @Subscribe
    public void handleNickChanges(NickChangeEvent<PircBotX> event) {
        accountManager.updateNick(event.getOldNick(), event.getNewNick());
    }

    @Subscribe
    public void handleUserLists(UserListEvent<PircBotX> event) {
        for (User user : event.getUsers()) {
            if (!user.equals(event.getBot().getUserBot())) {
                greet(user);
            }
        }
    }

    @Subscribe
    public void accountSecurity(PrivateMessageEvent<PircBotX> event) {
        final List<String> arguments = argumentsFor("!account:security", event.getMessage());
        if (arguments.isEmpty()) {
            return;
        }

        final String nickname = event.getUser().getNick();
        if (arguments.size() >= 2) {
            try {
                accountManager.toggleAccountSecurity(nickname, arguments.get(1));
                event.respond("Account password has been changed.");
            } catch (AuthenticationFailedException e) {
                event.respond("Sorry, your password doesn't match.");
            }
        } else if (arguments.size() == 1 && accountManager.isVerified(nickname)) {
            final Optional<Account> account = accountManager.getAccount(nickname);
            if (account.isPresent()) {
                event.respond("Account password set: " + format(account.get().getPassword().isPresent()));
            }
        }
    }

    private void greet(User user) {
        try {
            accountManager.login(user.getNick(), Optional.<AccountCredentials>absent(), false);
            user.send().message("Welcome back, " + user.getNick() + "!");
        } catch (NoSuchVisibleUserException ignored) {
        } catch (AuthenticationFailedException e) {
            createNewAccount(user);
        } catch (SecuredAccountException e) {
            user.send().message("This account has a password on it. You might need to !account:login to access some features.");
        }
    }

    private void createNewAccount(User user) {
        try {
            accountManager.createAccount(user.getNick());
            accountManager.login(user.getNick(), Optional.<AccountCredentials>absent(), false);
            user.send().message("Hello, " + user.getNick() + ". I've created a new account for you.");
        } catch (UsernameAlreadyExistsException e) {
            user.send().message("I tried to create an account for you, but it looks like your nickname already exists.");
        } catch (NoSuchVisibleUserException | SecuredAccountException | AuthenticationFailedException e) {
            log.warn("Tried to log in to account \"" + user.getNick() + "\" but failed", e);
            user.send().message("Hello, " + user.getNick() + ". You've got a new account with the name \"" + user.getNick() + "\" but I couldn't log you in. " +
                    "Try !account:login");
        }
    }

    private void refreshVerification(GenericUserEvent<PircBotX> event) {
        // OK to always log out here because we're currently only in one channel.
        accountManager.logout(event.getUser().getNick());
    }
}
