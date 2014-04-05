package com.sl5r0.qwobot.helpers;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;

public class VerifiableChannel extends Channel {
    protected VerifiableChannel(PircBotX bot, TestableUserChannelDao dao, String name) {
        super(bot, dao, name);
    }
}
