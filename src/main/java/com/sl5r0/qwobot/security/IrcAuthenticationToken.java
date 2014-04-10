package com.sl5r0.qwobot.security;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

import static com.google.common.base.Preconditions.checkNotNull;

public class IrcAuthenticationToken implements AuthenticationToken, RememberMeAuthenticationToken {
    private final String nickname;

    public IrcAuthenticationToken(String nickname) {
        this.nickname = checkNotNull(nickname, "nickname must not be null");
    }

    @Override
    public Object getPrincipal() {
        return nickname;
    }

    @Override
    public Object getCredentials() {
        return nickname;
    }

    @Override
    public boolean isRememberMe() {
        return false;
    }
}
