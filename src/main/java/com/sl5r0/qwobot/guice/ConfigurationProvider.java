package com.sl5r0.qwobot.guice;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;

import static com.google.common.base.Optional.fromNullable;
import static com.sl5r0.qwobot.guice.QwoBotModule.ConfigurationFilename;
import static org.slf4j.LoggerFactory.getLogger;

public class ConfigurationProvider implements Provider<HierarchicalConfiguration> {
    private static final Logger log = getLogger(ConfigurationProvider.class);
    private final XMLConfiguration configuration;

    @Inject
    public ConfigurationProvider(@ConfigurationFilename String filename) throws ConfigurationException {
        log.info("Loading configuration from " + filename);
        this.configuration = new XMLConfiguration(filename);
    }

    @Override
    public HierarchicalConfiguration get() {
        return new HierarchicalConfiguration(configuration);
    }

    public static Optional<String> readConfigurationValue(Configuration configuration, String key) {
        final Optional<String> value = fromNullable(configuration.getString(key));
        if (!value.isPresent()) {
            log.warn("Failed to read configuration value for key \"" + key + "\"");
        }
        return value;
    }
}
