package com.sl5r0.qwobot.plugins.commands;

import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;

/**
 * A command that accepts "on" and "off" values as arguments. Additional arguments are ignored.
 */
public abstract class ToggleCommand extends PrefixCommand {
    public static final String OFF = "OFF";
    public static final String ON = "ON";

    public ToggleCommand(String prefix) {
        super(prefix);
    }

    protected abstract void execute(MessageEvent event, boolean value);

    @Override
    protected final void execute(MessageEvent event, List<String> arguments) {
        if (arguments.size() < 1) {
            throwParsingError();
        }

        final String newValue = arguments.get(0).toLowerCase();
        switch (newValue) {
            case OFF:
                execute(event, false);
                break;
            case ON:
                execute(event, true);
                break;
            default:
                throwParsingError();
        }
    }

    @Override
    public final String getHelp() {
        return getPrefix() + " <" + ON + "|" + OFF + ">";
    }

}
