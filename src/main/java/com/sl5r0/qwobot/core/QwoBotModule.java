package com.sl5r0.qwobot.core;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.apache.commons.configuration.ConfigurationException;

public class QwoBotModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EventBus.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    public BotConfiguration provideBotConfiguration() {
        try {
            return new BotConfiguration();
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}