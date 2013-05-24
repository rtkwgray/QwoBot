package com.sl5r0.qwobot.plugins;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.sl5r0.qwobot.api.Plugin;
import com.sl5r0.qwobot.api.QwoBot;
import com.sl5r0.qwobot.api.QwoBotPlugin;
import com.sl5r0.qwobot.domain.MessageEvent;
import com.sl5r0.qwobot.domain.User;

import java.util.List;
import java.util.Map;

public class PluginInfo extends QwoBotPlugin {

    private static final String PLUGIN_DESCRIPTION = "Plugin for printing all loaded plugin information to either a user or channel.";
    private static final String PLUGIN_VERSION = "0.1";
    private static final String PLUGIN_TRIGGER = "!plugin";
    private static final String HELP_OPTION = "help";
    private static final String LIST_OPTION = "list";
    private static final String CONFIG_OPTION = "config";
    private static final String PLUGIN_NOT_FOUND = "No plugin was found with the specified name.";

    public PluginInfo(QwoBot qwoBot) {
        super(qwoBot);
    }

    @Override
    public String getDescription() {
        return PLUGIN_DESCRIPTION;
    }

    @Override
    public String getVersion() {
        return PLUGIN_VERSION;
    }

    @Override
    public String getHelp() {
        return "!plugin list - Show a list of loaded plugins\n" +
                "!plugin help <plugin name> - Show the help for a plugin\n" +
                "!plugin config <plugin name> - Show the configuration details for a plugin";
    }

    @Subscribe
    public void processMessageEvent(MessageEvent event) {
        List<String> parameters = getParametersFromEvent(event);
        if (parameters.size() < 2 || !parameters.get(0).equals(PLUGIN_TRIGGER)) {
            return;
        }

        switch (parameters.get(1)) {
            case LIST_OPTION:
                sendPluginListToUser(event.user);
                break;

            case HELP_OPTION:
                if (parameters.size() >= 3) {
                    sendPluginHelpToUser(event.user, parameters.get(2));
                }
                break;

            case CONFIG_OPTION:
                if (parameters.size() >= 3) {
                    sendPluginConfigToUser(event.user, parameters.get(2));
                }
        }
    }

    private void sendPluginConfigToUser(User user, String pluginName) {
        Plugin plugin = loadedPlugins().get(pluginName);
        if (plugin == null) {
            bot().sendMessageToUser(user, PLUGIN_NOT_FOUND);
            return;
        }

        bot().sendMessageToUser(user, "Configuration details for plugin: " + plugin.getName());
        bot().sendMessageToUser(user, plugin.getConfigurationInformation());
    }

    private void sendPluginHelpToUser(User user, String pluginName) {
        Plugin plugin = loadedPlugins().get(pluginName);
        if (plugin == null) {
            bot().sendMessageToUser(user, PLUGIN_NOT_FOUND);
            return;
        }

        bot().sendMessageToUser(user, "Help listing for plugin: " + plugin.getName());
        bot().sendMessageToUser(user, plugin.getHelp());
    }

    private void sendPluginListToUser(User user) {
        bot().sendMessageToUser(user, "The following plugins are loaded:");
        for (Plugin plugin : loadedPlugins().values()) {
            bot().sendMessageToUser(user, plugin.getName() + " (version " + plugin.getVersion() + "): " + plugin.getDescription());
        }
    }

    private Map<String, Plugin> loadedPlugins() {
        return Maps.uniqueIndex(bot().getLoadedPlugins(), new Function<com.sl5r0.qwobot.api.Plugin, java.lang.String>() {
            @Override
            public java.lang.String apply(com.sl5r0.qwobot.api.Plugin plugin) {
                return plugin.getName();
            }
        });
    }
}
