package com.sl5r0.qwobot.plugins.help;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.sl5r0.qwobot.plugins.PluginManager;
import com.sl5r0.qwobot.plugins.commands.Command;
import com.sl5r0.qwobot.plugins.commands.ParameterizedTriggerCommand;
import com.sl5r0.qwobot.plugins.exceptions.CommandExecutionException;
import com.sl5r0.qwobot.plugins.exceptions.PluginNotRegisteredException;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;
import java.util.Set;

public class ShowPluginHelp extends ParameterizedTriggerCommand {
    private static final String TRIGGER = "!help";
    private final PluginManager pluginManager;

    public ShowPluginHelp(PluginManager pluginManager) {
        super(TRIGGER, TO_LOWERCASE);
        this.pluginManager = pluginManager;
    }

    @Override
    public void execute(MessageEvent event, List<String> parameters) {
        if (parameters.isEmpty()) {
            throw new CommandExecutionException("No plugin specified for help documentation.");
        }

        final String pluginName = parameters.get(0);
        final Set<Command> commandsForPlugin;
        try {
            commandsForPlugin = pluginManager.getCommandsForPlugin(pluginName);
        } catch (PluginNotRegisteredException e) {
            throw new CommandExecutionException("Couldn't find plugin: " + pluginName);
        }

        event.getUser().send().message("Commands for plugin: " + pluginName);
        for (Command command : commandsForPlugin) {
            event.getUser().send().message(command.getHelp());
        }
    }

    @Override
    public String getHelp() {
        return TRIGGER + " <" + Joiner.on("|").join(pluginManager.getRegisteredPlugins()) + ">";
    }
}
