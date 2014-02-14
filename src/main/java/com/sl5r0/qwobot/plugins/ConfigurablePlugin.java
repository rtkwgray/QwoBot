package com.sl5r0.qwobot.plugins;

import com.google.inject.Inject;
import com.sl5r0.qwobot.core.BotConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.DefaultExpressionEngine;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ConfigurablePlugin extends Plugin {
    protected HierarchicalConfiguration config;

    @Inject
    @SuppressWarnings("UnusedDeclaration") // Used by Guice
    public final void setBotConfiguration(BotConfiguration config) throws ConfigurationException {
        config.setExpressionEngine(new XPathExpressionEngine());
        this.config = config.configurationAt("plugins/plugin[@class='" + getClass().getCanonicalName() + "']");
        this.config.setExpressionEngine(new DefaultExpressionEngine());
        validateConfiguration();
    }

    private void validateConfiguration() throws ConfigurationException {
        final Set<String> strings = checkNotNull(requiredConfigurationProperties(),
                "requiredConfigurationProperties() must not return null. Return an empty set instead.");

        for (String property : strings) {
            if (!config.containsKey(property)) {
                throw new ConfigurationException("Missing configuration for plugin " + getName() + ": " + property);
            }
        }
    }

    protected abstract Set<String> requiredConfigurationProperties();
}
