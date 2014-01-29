package com.sl5r0.qwobot.plugins.commands;

import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;

public interface ParameterCommand {
    public void execute(MessageEvent event, List<String> parameters);
}
