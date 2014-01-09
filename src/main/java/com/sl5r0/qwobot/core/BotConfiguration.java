package com.sl5r0.qwobot.core;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class BotConfiguration extends XMLConfiguration {
    private static final String CONFIG_FILE_PROPERTY = "qwobot.config.file";
    private static final String DEFAULT_CONFIG_FILE = "qwobot.xml";
    private static final String CONFIGURATION_FILE = System.getProperty(CONFIG_FILE_PROPERTY, DEFAULT_CONFIG_FILE);
    private static final Logger log = LoggerFactory.getLogger(BotConfiguration.class);

    public BotConfiguration() throws ConfigurationException {
        super(CONFIGURATION_FILE);
        log.info("Loaded configuration from " + CONFIGURATION_FILE);
        if (log.isDebugEnabled()) {
            final Iterator<String> configurationKeys = getKeys();
            while (configurationKeys.hasNext()) {
                final String key = configurationKeys.next();
                log.debug(key + " => " + getString(key));
            }
        }
    }
}
