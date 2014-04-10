package com.sl5r0.qwobot.core;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.Account;
import com.sl5r0.qwobot.irc.service.IrcBotService;
import com.sl5r0.qwobot.persistence.AccountRepository;
import com.sl5r0.qwobot.security.exceptions.AuthenticationFailedException;
import com.sl5r0.qwobot.security.exceptions.NoSuchVisibleUserException;
import com.sl5r0.qwobot.security.exceptions.SecuredAccountException;
import com.sl5r0.qwobot.security.exceptions.UsernameAlreadyExistsException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.pircbotx.User;
import org.slf4j.Logger;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.sl5r0.qwobot.security.Permissions.MODIFY_ACCOUNT;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class AccountManager {
    private static final Logger log = getLogger(AccountManager.class);
    private final AccountRepository accountRepository;
    private final IrcBotService ircBotService;
    private final BiMap<Long, String> verifiedNicknames = HashBiMap.create();

    @Inject
    public AccountManager(AccountRepository accountRepository, IrcBotService ircBotService) {
        this.ircBotService = checkNotNull(ircBotService, "ircBotService must not be null");
        this.accountRepository = checkNotNull(accountRepository, "accountRepository must not be null");
    }

    public Optional<Account> getAccount(String nickname) {
        final Optional<Long> verifiedAccountId = fromNullable(verifiedNicknames.inverse().get(nickname));
        if (verifiedAccountId.isPresent()) {
            return accountRepository.findById(verifiedAccountId.get());
        } else {
            return accountRepository.findByNick(nickname);
        }
    }

    public boolean isVerified(String nickname) {
        checkNotNull(nickname, "nickname must not be null");
        return verifiedNicknames.values().contains(nickname);
    }

    public void updateNick(String oldNickname, String newNickname) {
        checkNotNull(oldNickname, "oldNickname must not be null");
        checkNotNull(newNickname, "newNickname must not be null");

        final Optional<Long> existingAccount = fromNullable(verifiedNicknames.inverse().get(oldNickname));
        if (existingAccount.isPresent()) {
            verifiedNicknames.put(existingAccount.get(), newNickname);
            log.info("Verified user \"" + oldNickname + "\" changed nickname to \"" + newNickname + "\"");
        } else {
            log.warn("Unverified user \"" + oldNickname + "\" tried to change nickname to \"" + newNickname + "\"");
        }
    }

    public void login(String currentNick, Optional<AccountCredentials> credentials, boolean requireVisibilty) throws NoSuchVisibleUserException, AuthenticationFailedException, SecuredAccountException {
        checkNotNull(credentials, "credentials must not be null");
        checkNotNull(currentNick, "currentNick must not be null");

        if (requireVisibilty && !userIsVisible(currentNick)) {
            log.warn("Authentication failed for \"" + currentNick + "\" because they're not visible.");
            throw new NoSuchVisibleUserException();
        }

        logout(currentNick);
        if (credentials.isPresent()) {
            loginWithCredentials(credentials, currentNick);
            log.info("Account \"" + credentials.get().getUsername() + "\" logged in with nickname \"" + currentNick + "\"");
        } else {
            loginWithoutCredentials(currentNick);
            log.info("Account \"" + currentNick + "\" logged in without password");
        }
    }

    private void loginWithCredentials(Optional<AccountCredentials> credentials, String currentNick) throws AuthenticationFailedException {
        final Optional<Account> account = accountRepository.findByUsernamePassword(credentials.get().getUsername(), credentials.get().getPassword());
        if (account.isPresent()) {
            verifiedNicknames.put(account.get().getId(), currentNick);
            return;
        }

        throw new AuthenticationFailedException();
    }

    private void loginWithoutCredentials(String currentNick) throws SecuredAccountException, AuthenticationFailedException {
        final Optional<Account> account = accountRepository.findByNick(currentNick);
        if (account.isPresent()) {
            if (account.get().getPassword().isPresent()) {
                throw new SecuredAccountException();
            } else {
                verifiedNicknames.put(account.get().getId(), currentNick);
                return;
            }
        }

        throw new AuthenticationFailedException();
    }

    public void logout(String nickname) {
        checkNotNull(nickname, "nickname must not be null");
        verifiedNicknames.inverse().remove(nickname);
        log.info("IRC user \"" + nickname + "\" has been logged out.");
    }

    public boolean userIsVisible(String currentNick) {
        final User user = ircBotService.getBot().getUserChannelDao().getUser(currentNick);
        return !user.getChannels().isEmpty();
    }

    public void createAccount(String username) throws UsernameAlreadyExistsException {
        final Optional<Account> existingAccount = accountRepository.findByNick(username);
        if (existingAccount.isPresent()) {
            log.warn("Couldn't create account for \"" + username + "\" because it already exists.");
            throw new UsernameAlreadyExistsException();
        }

        accountRepository.save(new Account(username));
        log.info("Created account for \"" + username + "\"");
    }

    @RequiresPermissions(MODIFY_ACCOUNT)
    public void toggleAccountSecurity(String nickname, String password) throws AuthenticationFailedException {
        checkNotNull(nickname, "nickname must not be null");
        checkNotNull(password, "password must not be null");

        final Optional<Account> account = getAccount(nickname);
        if (account.isPresent()) {
            final Optional<String> existingPassword = account.get().getPassword();
            if (existingPassword.isPresent()) {
                if (existingPassword.get().equals((password))) {
                    account.get().setPassword(null);
                } else {
                    throw new AuthenticationFailedException();
                }
            } else {
                account.get().setPassword(password);
            }

            accountRepository.saveOrUpdate(account.get());
        }
    }
}
