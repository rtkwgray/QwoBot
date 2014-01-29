package com.sl5r0.qwobot.plugins.commands;

import org.pircbotx.hooks.events.MessageEvent;

public abstract class TriggerCommand extends MessageCommand {
    private final String trigger;

    public TriggerCommand(String trigger) {
        this.trigger = trigger;
    }

    protected final String getTrigger() {
        return trigger;
    }

    @Override
    protected final void onMessageEvent(MessageEvent event) {
        if (event.getMessage().startsWith(trigger)) {
            triggered(event);
        }
    }

    protected abstract void triggered(MessageEvent event);
}
