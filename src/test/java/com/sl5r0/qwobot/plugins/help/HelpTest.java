package com.sl5r0.qwobot.plugins.help;

import org.junit.Test;

public class HelpTest {
    @Test(expected = NullPointerException.class)
    public void ensurePluginManagerCannotBeNull() throws Exception {
        new Help(null);
    }
}
