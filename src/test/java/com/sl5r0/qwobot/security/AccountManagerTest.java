package com.sl5r0.qwobot.security;

import junit.framework.TestCase;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

/**
 * Created by Warren on 4/11/2014.
 */
public class AccountManagerTest extends TestCase {
    public void testName() throws Exception {
        PircBotX bot = new PircBotX(new Configuration.Builder<>().setServerHostname("1.2.3.4").buildConfiguration());
        bot.getUserChannelDao().getUser("something").getChannels();
    }
}
