package com.sl5r0.qwobot.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;

import static com.sl5r0.qwobot.guice.QwoBotModule.ConfigurationFilename;
import static org.slf4j.LoggerFactory.getLogger;

public class ConfigurationProvider implements Provider<Configuration> {
    private static final Logger log = getLogger(ConfigurationProvider.class);
    private final Configuration configuration;

    @Inject
    public ConfigurationProvider(@ConfigurationFilename String filename) throws ConfigurationException {
        log.info("Loading configuration from " + filename);
        this.configuration = new XMLConfiguration(filename);
    }

    @Override
    public Configuration get() {
        return configuration;
    }
}
