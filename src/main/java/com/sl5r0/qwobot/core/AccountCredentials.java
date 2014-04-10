package com.sl5r0.qwobot.core;

import static com.google.common.base.Preconditions.checkNotNull;

public class AccountCredentials {
    private final String username;
    private final String password;

    public AccountCredentials(String username, String password) {
        this.username = checkNotNull(username, "username must not be null");
        this.password = checkNotNull(password, "password must not be null");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
