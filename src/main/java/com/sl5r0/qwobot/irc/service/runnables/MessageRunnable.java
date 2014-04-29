package com.sl5r0.qwobot.irc.service.runnables;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.List;

/**
 * Represents an arbitrary chunk of code to be run in response to a {@link org.pircbotx.hooks.types.GenericMessageEvent}
 */
public interface MessageRunnable {
    void run(GenericMessageEvent<PircBotX> event, List<String> arguments);
}
