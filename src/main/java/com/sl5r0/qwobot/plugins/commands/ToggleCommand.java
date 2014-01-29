package com.sl5r0.qwobot.plugins.commands;

import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;

/**
 * A command that accepts "on" and "off" values as arguments. Additional arguments are ignored.
 */
public abstract class ToggleCommand extends PrefixCommand {

    public ToggleCommand(String prefix) {
        super(prefix, TO_LOWERCASE);
    }

    protected abstract void execute(MessageEvent event, boolean value);

    @Override
    protected final void execute(MessageEvent event, List<String> arguments) {
        if (arguments.size() < 1) {
            throwParsingError();
        }

        final String newValue = arguments.get(0);
        switch (newValue) {
            case "off":
                execute(event, false);
                break;
            case "on":
                execute(event, true);
                break;
            default:
                throwParsingError();
        }
    }

    @Override
    public final String getHelp() {
        return getPrefix() + " <on|off>";
    }
}
