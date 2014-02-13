package com.sl5r0.qwobot.plugins.help;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sl5r0.qwobot.plugins.Plugin;
import com.sl5r0.qwobot.plugins.PluginManager;
import com.sl5r0.qwobot.plugins.commands.Command;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class Help extends Plugin {
    private final Set<Command> commands;

    @Inject
    public Help(Provider<PluginManager> pluginManagerProvider) {
        checkNotNull(pluginManagerProvider, "pluginManagerProvider cannot be null");
        commands = ImmutableSet.<Command>of(new ShowPluginHelp(pluginManagerProvider));
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
