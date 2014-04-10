package com.sl5r0.qwobot.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.core.AccountManager;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sl5r0.qwobot.security.Permissions.ACCOUNT;

@Singleton
public class QwoBotRealm extends AuthorizingRealm {
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

        final String nickname = (String) principals.getPrimaryPrincipal();

        final SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        if (accountManager.isVerified(nickname)) {
            authorizationInfo.addStringPermission(ACCOUNT);
        }

        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken token) throws AuthenticationException {
        return new AuthenticationInfo() {
            @Override
            public PrincipalCollection getPrincipals() {
                return new SimplePrincipalCollection(token.getPrincipal(), getName());
            }

            @Override
            public Object getCredentials() {
                return token.getCredentials();
            }
        };
    }
}
