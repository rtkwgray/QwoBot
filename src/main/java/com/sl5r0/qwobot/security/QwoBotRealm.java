package com.sl5r0.qwobot.security;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.core.UserManager;
import com.sl5r0.qwobot.domain.QwobotUser;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.pircbotx.User;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sl5r0.qwobot.domain.Role.securedAccount;

@Singleton
public class QwoBotRealm extends AuthorizingRealm {
    private final UserManager userManager;

    @Inject
    public QwoBotRealm(UserManager userManager) {
        this.userManager = checkNotNull(userManager, "userManager must not be null");
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

        final QwobotUser user = (QwobotUser) principals.getPrimaryPrincipal();
        final boolean accountIsInsecure = !user.hasRole(securedAccount());

        final SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        if (accountIsInsecure || userManager.hasVerifiedUser(user.getId())) {
            authorizationInfo.addStringPermission("account");
        }

        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken token) throws AuthenticationException {
        if (token.getPrincipal() instanceof User) {
            final Optional<QwobotUser> user = userManager.getUser((User) token.getPrincipal());
            if (user.isPresent()) {
                return new AuthenticationInfo() {
                    @Override
                    public PrincipalCollection getPrincipals() {
                        return new SimplePrincipalCollection(user.get(), getName());
                    }

                    @Override
                    public Object getCredentials() {
                        return token.getCredentials();
                    }
                };
            }
        }
        return null;
    }
}
