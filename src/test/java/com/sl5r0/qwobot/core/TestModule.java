package com.sl5r0.qwobot.core;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.sl5r0.qwobot.persistence.SessionFactoryCreator;
import org.apache.commons.configuration.ConfigurationException;

import java.io.File;
import java.net.URISyntaxException;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.io.Resources.getResource;
import static com.google.inject.Guice.createInjector;
import static com.sl5r0.qwobot.persistence.SessionFactoryCreator.SchemaRule.CREATE_DROP;

public class TestModule extends QwoBotModule {
    private final Optional<String> configurationLocation;
    private final Optional<EventBus> eventBus;

    public TestModule(String configurationLocation, EventBus eventBus) {
        this.eventBus = fromNullable(eventBus);
        this.configurationLocation = fromNullable(configurationLocation);
    }

    @Override
    protected SessionFactoryCreator createSessionFactoryCreator() {
        return new SessionFactoryCreator("test", CREATE_DROP);
    }

    @Override
    protected File getBotConfigurationFile() throws ConfigurationException {
        if (configurationLocation.isPresent()) {
            try {
                return new File(getResource(configurationLocation.get()).toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return super.getBotConfigurationFile();
    }

    @Override
    protected void bindEventBus() {
        if (eventBus.isPresent()) {
            bind(EventBus.class).toInstance(eventBus.get());
        } else {
            super.bindEventBus();
        }
    }

    public static ModuleBuilder testInjector() {
        return new ModuleBuilder();
    }

    public static class ModuleBuilder {
        private String configFile;
        private EventBus eventBus;

        public <T> T instanceOf(Class<T> clazz) {
            return createInjector(new TestModule(configFile, eventBus)).getInstance(clazz);
        }

        public ModuleBuilder withConfiguration(String configFile) {
            this.configFile = configFile;
            return this;
        }

        public ModuleBuilder withEventBus(EventBus eventBus) {
            this.eventBus = eventBus;
            return this;
        }
    }
}
