package com.sl5r0.qwobot.core;

import com.sl5r0.qwobot.security.AccountCredentials;
import org.junit.Test;

public class AccountCredentialsTest {
    @Test(expected = NullPointerException.class)
    public void ensureUsernameCannotBeNull() throws Exception {
        new AccountCredentials(null, "password");
    }

    @Test(expected = NullPointerException.class)
    public void ensurePasswordCannotBeNull() throws Exception {
        new AccountCredentials("username", null);
    }
}
