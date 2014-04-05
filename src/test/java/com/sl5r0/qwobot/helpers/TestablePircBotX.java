package com.sl5r0.qwobot.helpers;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

public class TestablePircBotX extends PircBotX {
    public TestablePircBotX(Configuration<? extends PircBotX> configuration) {
        super(configuration);
    }
}
