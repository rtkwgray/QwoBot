package com.sl5r0.qwobot.plugins.commands;

import com.google.common.eventbus.Subscribe;
import com.sl5r0.qwobot.plugins.exceptions.CommandExecutionException;
import org.pircbotx.hooks.events.MessageEvent;

public abstract class MessageCommand implements Command {

    @Subscribe
    protected abstract void onMessageEvent(MessageEvent event);

    protected final void throwParsingError() {
        throw new CommandExecutionException("Sorry, I don't understand.");
    }
}
