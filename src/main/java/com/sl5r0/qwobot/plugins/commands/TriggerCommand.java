package com.sl5r0.qwobot.plugins.commands;

import org.pircbotx.hooks.events.MessageEvent;

/**
 * A Command that is executed when the a message starts with the trigger.
 */
public abstract class TriggerCommand extends MessageCommand {
    private final String trigger;

    public TriggerCommand(String trigger) {
        this.trigger = trigger;
    }

    protected final String getTrigger() {
        return trigger;
    }

    @Override
    public final void onMessageEvent(MessageEvent event) {
        if (event.getMessage().startsWith(trigger)) {
            triggered(event);
        }
    }

    @Override
    public String getHelp() {
        return trigger;
    }

    protected abstract void triggered(MessageEvent event);
}
