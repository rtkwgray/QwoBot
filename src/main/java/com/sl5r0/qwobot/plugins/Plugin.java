package com.sl5r0.qwobot.plugins;

import com.sl5r0.qwobot.plugins.commands.Command;

import java.util.Set;

/**
 * A Plugin represents a collection of commands and commands that respond to those commands. Simple plugins will likely
 * only have one command (and thus one trigger), while more complex plugins should create a state object that is passed
 * into commands so that they can manipulate it.
 */
public abstract class Plugin {
    /**
     * Plugins should use this method to initialize all commands.
     * @return a set of {@link Command} to be registered.
     */
    public abstract Set<Command> getCommands();

    /**
     * @return a string representing the plugin's version.
     */
    public abstract String getVersion();

    public final String getName() {
        return getClass().getSimpleName();
    }

    public String toString() {
        return getName() + " v" + getVersion();
    }
}
