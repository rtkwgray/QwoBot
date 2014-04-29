package com.sl5r0.qwobot.security;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.Account;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.pircbotx.User;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class QwoBotRealm extends AuthorizingRealm {
    private static final Logger log = getLogger(QwoBotRealm.class);
    private final AccountManager accountManager;

    @Inject
    public QwoBotRealm(AccountManager accountManager) {
        this.accountManager = checkNotNull(accountManager, "accountManager must not be null");
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof IrcAuthenticationToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals.isEmpty()) {
            return null;
        }

        final Account account = (Account) principals.getPrimaryPrincipal();
        return new SimpleAuthorizationInfo(account.getRoles());
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken token) throws AuthenticationException {
        // We're only ever going to be passed an IrcAuthenticationToken here because of the supports() method above.
        final User user = (User) token.getPrincipal();
        final Optional<Account> account = accountManager.getAccount(user);
        if (account.isPresent()) {
            log.trace("Authenticated user \"" + account.get().getUsername() + "\" with nickname \"" + user.getNick() + "\"");
            return new AuthenticationInfo() {
                @Override
                public PrincipalCollection getPrincipals() {
                    return new SimplePrincipalCollection(account.get(), getName());
                }

                @Override
                public Object getCredentials() {
                    return token.getCredentials();
                }
            };
        }

        return null;
    }
}
