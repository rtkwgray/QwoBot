package com.sl5r0.qwobot.helpers;

import org.pircbotx.*;

public class TestableUserChannelDao extends UserChannelDao<User, Channel> {
    public TestableUserChannelDao(PircBotX bot) {
        super(bot, new Configuration.BotFactory());
    }
}
