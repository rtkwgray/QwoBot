package com.sl5r0.qwobot.plugins;

import com.sl5r0.qwobot.plugins.commands.Command;

import java.util.Set;

/**
 * A Plugin represents a collection of commands and commands that respond to those commands. Simple plugins will likely
 * only have one command (and thus one trigger), while more complex plugins should create a state object that is passed
 * into commands so that they can manipulate it.
 */
public abstract class Plugin {
    public abstract Set<Command> getCommands();

    public abstract String getVersion();

    public String getName() {
        return getClass().getSimpleName();
    }

    public String toString() {
        return getName() + " v" + getVersion();
    }

}