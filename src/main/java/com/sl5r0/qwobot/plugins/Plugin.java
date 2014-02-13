package com.sl5r0.qwobot.plugins;

import com.sl5r0.qwobot.core.BotConfiguration;
import com.sl5r0.qwobot.plugins.commands.Command;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.DefaultExpressionEngine;
import org.apache.commons.configuration.tree.ExpressionEngine;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

import java.util.Set;

/**
 * A Plugin represents a collection of commands and commands that respond to those commands. Simple plugins will likely
 * only have one command (and thus one trigger), while more complex plugins should create a state object that is passed
 * into commands so that they can manipulate it.
 */
public abstract class Plugin {
    public abstract Set<Command> getCommands();

    public abstract String getVersion();

    public final String getName() {
        return getClass().getSimpleName();
    }

    public String toString() {
        return getName() + " v" + getVersion();
    }

    protected final HierarchicalConfiguration getPluginConfiguration(BotConfiguration configuration) {
        final ExpressionEngine originalExpressionEngine = configuration.getExpressionEngine();
        configuration.setExpressionEngine(new XPathExpressionEngine());

        final HierarchicalConfiguration pluginConfig;
        try {
            pluginConfig = configuration.configurationAt("plugins/plugin[@class='" + getClass().getCanonicalName() + "']");
            pluginConfig.setExpressionEngine(originalExpressionEngine);
        } finally {
            configuration.setExpressionEngine(originalExpressionEngine);
        }

        return pluginConfig;
    }
}
