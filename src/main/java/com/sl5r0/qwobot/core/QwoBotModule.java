package com.sl5r0.qwobot.core;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.persistence.SessionFactoryCreator;
import com.sl5r0.qwobot.plugins.PluginManager;
import org.apache.commons.configuration.ConfigurationException;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.sl5r0.qwobot.persistence.SessionFactoryCreator.SchemaRule.UPDATE;
import static java.lang.annotation.ElementType.*;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class QwoBotModule extends AbstractModule {
    private static final String CONFIG_FILE_PROPERTY = "qwobot.config.file";
    private static final String DEFAULT_CONFIG_FILE = "qwobot.xml";

    @Override
    protected void configure() {
        bind(PluginManager.class).asEagerSingleton();
    }

    @Singleton
    @Provides @BotConfigFile
    public final File provideBotConfiguration() throws ConfigurationException {
        return getBotConfigurationFile();
    }

    protected EventBus getEventBus() {
        return new AsyncEventBus(newFixedThreadPool(10));
    }

    @Singleton
    @Provides
    public EventBus provideEventBus() {
        return getEventBus();
    }

    @BindingAnnotation
    @Target({PARAMETER, METHOD, FIELD}) @Retention(RetentionPolicy.RUNTIME)
    public @interface BotConfigFile {}

    @Provides
    public final SessionFactoryCreator sessionFactoryCreator() {
        return createSessionFactoryCreator();
    }

    protected SessionFactoryCreator createSessionFactoryCreator() {
        return new SessionFactoryCreator("", UPDATE);
    }

    protected File getBotConfigurationFile() throws ConfigurationException {
        final String configFileLocation = System.getProperty(CONFIG_FILE_PROPERTY, DEFAULT_CONFIG_FILE);
        final File config = new File(configFileLocation);
        if (!config.exists()) {
            throw new ConfigurationException("Could not load configuration from " + config.getAbsolutePath());
        }
        return config;
    }

//    Configuration configuration = new Configuration();
//    configuration = configuration.setProperty("hibernate.connection.url", "jdbc:h2:datastores/test2");
//    configuration = configuration.setProperty("hibernate.connection.driver_class", Driver.class.getCanonicalName());
//    configuration = configuration.setProperty("hibernate.dialect", H2Dialect.class.getCanonicalName());
//    configuration = configuration.setProperty("hibernate.hbm2ddl.auto", "update"); // create schema if it doesn't exist
//    configuration = configuration.addAnnotatedClass(User.class);
//    StandardServiceRegistryBuilder bootstrapServiceRegistryBuilder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
//    SessionFactory sessionFactory = configuration.buildSessionFactory(bootstrapServiceRegistryBuilder.build());




}
