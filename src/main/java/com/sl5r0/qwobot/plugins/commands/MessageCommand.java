package com.sl5r0.qwobot.plugins.commands;

import com.sl5r0.qwobot.plugins.exceptions.CommandExecutionException;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;

public abstract class MessageCommand implements Command {
    protected abstract void execute(MessageEvent event, List<String> arguments);
    protected final void throwParsingError() {
        throw new CommandExecutionException("Sorry, I don't understand.");
    }
}
