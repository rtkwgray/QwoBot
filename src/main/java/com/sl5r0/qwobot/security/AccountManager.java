package com.sl5r0.qwobot.security;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.Account;
import com.sl5r0.qwobot.persistence.AccountRepository;
import com.sl5r0.qwobot.security.exceptions.AccountHasPasswordException;
import com.sl5r0.qwobot.security.exceptions.BotCannotSeeUserException;
import com.sl5r0.qwobot.security.exceptions.IncorrectPasswordException;
import com.sl5r0.qwobot.security.exceptions.UsernameAlreadyExistsException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.pircbotx.User;
import org.slf4j.Logger;

import java.util.Set;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.tryFind;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class AccountManager {
    private static final Logger log = getLogger(AccountManager.class);
    private final AccountRepository accountRepository;
    private final BiMap<Long, User> verifiedUsers = HashBiMap.create();

    @Inject
    public AccountManager(AccountRepository accountRepository) {
        this.accountRepository = checkNotNull(accountRepository, "accountRepository must not be null");
    }

    public Set<Long> getAuthenticatedUserIds() {
        return copyOf(verifiedUsers.keySet());
    }

    public static Account getActingAccount() {
        final Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated() && subject.getPrincipal() instanceof Account) {
            return (Account) subject.getPrincipal();
        }

        throw new UnauthenticatedException("Not logged in");
    }

    Optional<Account> getAccount(User user) {
        checkNotNull(user, "user must not be null");
        if (!userSharesChannelWithBot(user)) {
            final Long removed = verifiedUsers.inverse().remove(user);
            if (removed != null) {
                log.debug("Removed " + user.getNick() + " from the user map they share no channels with the bot.");
            }
            throw new BotCannotSeeUserException();
        }

        final Optional<Long> verifiedAccountId = fromNullable(verifiedUsers.inverse().get(user));
        if (verifiedAccountId.isPresent()) {
            return accountRepository.findById(verifiedAccountId.get());
        }

        // The user isn't verified, so they should be considered anonymous.
        return absent();
    }

    private boolean userSharesChannelWithBot(User user) {
        return !user.getBot().getUserChannelDao().getUser(user.getNick()).getChannels().isEmpty();
    }

    public void login(User user, AccountCredentials credentials) throws IncorrectPasswordException, BotCannotSeeUserException {
        checkArgument(!user.equals(user.getBot().getUserBot()), "cannot log in as the irc bot");
        checkNotNull(user, "user must not be null");
        checkNotNull(credentials, "credentials must not be null");
        if (!userSharesChannelWithBot(user)) {
            throw new BotCannotSeeUserException();
        }

        final Optional<Account> account = accountRepository.findByUsernamePassword(credentials.username(), credentials.password());
        if (account.isPresent()) {
            notifyPreviouslyLoggedInUser(user, account);
            verifiedUsers.inverse().remove(user);
            verifiedUsers.put(account.get().getId(), user);
            log.info("Logged in " + account.get().getUsername() + " with nickname " + user.getNick());
        } else {
            throw new IncorrectPasswordException();
        }
    }

    private void notifyPreviouslyLoggedInUser(User user, Optional<Account> account) {
        final Optional<User> previouslyLoggedInUser = fromNullable(verifiedUsers.remove(account.get().getId()));
        if (previouslyLoggedInUser.isPresent() && !previouslyLoggedInUser.get().equals(user)) {
            previouslyLoggedInUser.get().send().message("You have been logged out by a user with the nickname \"" + user.getNick() + "\"");
            log.info("User " + previouslyLoggedInUser.get() + " was logged out nickname " + user.getNick());
        }
    }

    public void login(User user) throws AccountHasPasswordException, IncorrectPasswordException, BotCannotSeeUserException {
        checkArgument(!user.equals(user.getBot().getUserBot()), "cannot log in as the irc bot");
        if (!userSharesChannelWithBot(user)) {
            throw new BotCannotSeeUserException();
        }

        final Optional<Account> account = accountRepository.findByNick(user.getNick());
        if (account.isPresent()) {
            if (account.get().getPassword().isPresent()) {
                throw new AccountHasPasswordException();
            } else {
                notifyPreviouslyLoggedInUser(user, account);
                verifiedUsers.inverse().remove(user);
                verifiedUsers.put(account.get().getId(), user);
                log.info("Logged in " + account.get().getUsername() + " with nickname " + user.getNick());
            }
        } else {
            throw new IncorrectPasswordException();
        }
    }

    public void logOutUserIfNotVisible(final User user) {
        final Optional<User> verifiedUser = tryFind(verifiedUsers.values(), new Predicate<User>() {
            @Override
            public boolean apply(User input) {
                return input.getNick().equals(user.getNick()) && input.getChannels().isEmpty();
            }
        });

        if (verifiedUser.isPresent()) {
            final Long removedId = verifiedUsers.inverse().remove(verifiedUser.get());
            log.info("Logged out user with id " + removedId + " and nickname \"" + user.getNick() + "\"");
        }
    }

    public void createAccount(String username) throws UsernameAlreadyExistsException {
        final Optional<Account> existingAccount = accountRepository.findByNick(username);
        if (existingAccount.isPresent()) {
            log.warn("Couldn't create account for \"" + username + "\" because it already exists.");
            throw new UsernameAlreadyExistsException();
        }

        final Account account = new Account(username);
        account.setBalance(10);
        accountRepository.save(account);
        log.info("Created account for \"" + username + "\"");
    }

    @RequiresAuthentication
    public void setAccountPassword(String password) throws IncorrectPasswordException {
        checkNotNull(password, "password must not be null");
        final Account account = getActingAccount();
        if (account.getPassword().isPresent()) {
            if (account.getPassword().get().equals(password)) {
                account.setPassword(null); // Remove password
            } else {
                throw new IncorrectPasswordException();
            }
        } else {
            account.setPassword(password);
        }

        accountRepository.saveOrUpdate(account);
    }

    public void logOutAllUsers() {
        verifiedUsers.clear();
    }
}
