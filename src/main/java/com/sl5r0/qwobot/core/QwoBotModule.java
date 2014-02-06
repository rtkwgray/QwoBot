package com.sl5r0.qwobot.core;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.apache.commons.configuration.ConfigurationException;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

public class QwoBotModule extends AbstractModule {
    private static final String CONFIG_FILE_PROPERTY = "qwobot.config.file";
    private static final String DEFAULT_CONFIG_FILE = "qwobot.xml";

    @Override
    protected void configure() {
        bind(EventBus.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    @BotConfigFile
    public File provideBotConfiguration() throws ConfigurationException{
        final String configFileLocation = System.getProperty(CONFIG_FILE_PROPERTY, DEFAULT_CONFIG_FILE);
        final File config = new File(configFileLocation);
        if (!config.exists()) {
            throw new ConfigurationException("Could not load configuration from " + config.getAbsolutePath());
        }
        return config;
    }

    @BindingAnnotation
    @Target({PARAMETER, METHOD, FIELD}) @Retention(RetentionPolicy.RUNTIME)
    public @interface BotConfigFile {}
}
