package com.sl5r0.qwobot.domain.command;

import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.List;

/**
* Created by Warren on 4/23/2014.
*/
public interface CommandHandler<X extends GenericMessageEvent> {
    void handle(X event, List<String> arguments);
}
