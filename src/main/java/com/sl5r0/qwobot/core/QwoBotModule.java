package com.sl5r0.qwobot.core;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.guice.ConfigurationProvider;
import com.sl5r0.qwobot.guice.PircBotConfigurationProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.System.getProperty;
import static java.lang.annotation.ElementType.*;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class QwoBotModule extends AbstractModule {
    private static final String CONFIG_FILE_PROPERTY = "config.file";
    private static final String DEFAULT_CONFIG_FILE = "qwobot.xml";

    @Override
    protected void configure() {
        bind(org.apache.commons.configuration.Configuration.class).toProvider(ConfigurationProvider.class).asEagerSingleton();
        bind(org.pircbotx.Configuration.class).toProvider(PircBotConfigurationProvider.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    public EventBus eventBus() {
        return getEventBus();
    }

    @Provides
    @Singleton
    @ConfigurationFilename
    public String configurationFilename() {
        return getProperty(CONFIG_FILE_PROPERTY, DEFAULT_CONFIG_FILE);
    }

    protected EventBus getEventBus() {
        return new AsyncEventBus(newFixedThreadPool(10));
    }

    protected String schemaCreationStrategy() {
        return "update";
    }

    @BindingAnnotation
    @Target({PARAMETER, METHOD, FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ConfigurationFilename {
    }
}
