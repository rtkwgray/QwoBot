package com.sl5r0.qwobot.irc.command;

import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.List;

public interface CommandHandler<X extends GenericMessageEvent> {
    void handle(X event, List<String> arguments);
}
