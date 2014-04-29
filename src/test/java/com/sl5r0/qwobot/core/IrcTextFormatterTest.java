package com.sl5r0.qwobot.core;

import org.junit.Test;

import static com.sl5r0.qwobot.core.IrcTextFormatter.RAINBOW;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.pircbotx.Colors.*;

public class IrcTextFormatterTest {
    @Test
    public void ensureMultipleColorFormatsWork() throws Exception {
        final String rainbowize = RAINBOW.format("message");
        assertThat(rainbowize, equalTo(PURPLE + "m" + BLUE + "e" + CYAN + "s" + GREEN + "s" + YELLOW + "a" + RED + "g" + PURPLE + "e" + NORMAL));
    }

    @Test
    public void ensureSingleColorFormatsWork() throws Exception {
        final String rainbowize = IrcTextFormatter.BLUE.format("message");
        assertThat(rainbowize, equalTo(BLUE + "message" + NORMAL));
    }
}
