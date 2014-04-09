package com.sl5r0.qwobot.security;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;
import org.pircbotx.User;

import static com.google.common.base.Preconditions.checkNotNull;

public class IrcAuthenticationToken implements AuthenticationToken, RememberMeAuthenticationToken {
    private final User user;

    public IrcAuthenticationToken(User user) {
        this.user = checkNotNull(user, "user must not be null");
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public Object getCredentials() {
        return user;
    }

    @Override
    public boolean isRememberMe() {
        return true;
    }
}
