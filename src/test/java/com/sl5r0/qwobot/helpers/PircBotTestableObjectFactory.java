package com.sl5r0.qwobot.helpers;

import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

public class PircBotTestableObjectFactory {
    private final PircBotX bot;
    private final Channel channel;
    private final User user;

    public PircBotTestableObjectFactory() {
        bot = new PircBotX(new Configuration.Builder<>().setServerHostname("localhost").buildConfiguration());

//        when(bot.getUserChannelDao()).thenReturn(new UserChannelDao<>(bot, mock(Configuration.BotFactory.class)));
        channel = bot.getUserChannelDao().getChannel("#default");
        user = bot.getUserChannelDao().getUser("nickname");
    }

    public MessageEvent<PircBotX> messageEvent(String message) {
       return new MessageEvent<>(bot, channel, user, message);
    }
}
