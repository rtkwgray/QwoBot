package com.sl5r0.qwobot.plugins.commands;

import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;

public abstract class MessageCommand implements Command {
    protected abstract void execute(MessageEvent event, List<String> arguments);
}
