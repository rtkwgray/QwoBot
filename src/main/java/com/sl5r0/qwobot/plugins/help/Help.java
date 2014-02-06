package com.sl5r0.qwobot.plugins.help;

import com.google.common.collect.ImmutableSet;
import com.sl5r0.qwobot.plugins.Plugin;
import com.sl5r0.qwobot.plugins.PluginManager;
import com.sl5r0.qwobot.plugins.commands.Command;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class Help extends Plugin {
    private final Set<Command> commands;

    public Help(PluginManager pluginManager) {
        checkNotNull(pluginManager, "pluginManager cannot be null");
        commands = ImmutableSet.<Command>of(new ShowPluginHelp(pluginManager));
    }

    @Override
    public Set<Command> getCommands() {
        return commands;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
