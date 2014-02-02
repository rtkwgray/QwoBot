package com.sl5r0.qwobot.core;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.pircbotx.Colors.*;

public class FormatTest {
    @Test
    public void ensureMultipleColorFormatsWork() throws Exception {
        String rainbowize = Format.RAINBOW.format("message");
        assertThat(rainbowize, equalTo(PURPLE + "m" + BLUE + "e" + CYAN + "s" + GREEN + "s" + YELLOW + "a" + RED + "g" + PURPLE + "e"));
    }

    @Test
    public void ensureSingleColorFormatsWork() throws Exception {
        String rainbowize = Format.BLUE.format("message");
        assertThat(rainbowize, equalTo(BLUE + "message"));
    }
}
