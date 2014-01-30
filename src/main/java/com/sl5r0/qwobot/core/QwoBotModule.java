package com.sl5r0.qwobot.core;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.apache.commons.configuration.ConfigurationException;

import java.io.File;

public class QwoBotModule extends AbstractModule {
    private static final String CONFIG_FILE_PROPERTY = "qwobot.config.file";
    private static final String DEFAULT_CONFIG_FILE = "qwobot.xml";

    @Override
    protected void configure() {
        bind(EventBus.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    public BotConfiguration provideBotConfiguration() throws ConfigurationException{
        final String configFileLocation = System.getProperty(CONFIG_FILE_PROPERTY, DEFAULT_CONFIG_FILE);
        final File configFile = new File(configFileLocation);
        return new BotConfiguration(configFile);
    }
}
