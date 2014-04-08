package com.sl5r0.qwobot.guice;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.*;
import com.sl5r0.qwobot.domain.ChatLog;
import com.sl5r0.qwobot.domain.QwobotUser;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.h2.Driver;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;
import org.pircbotx.PircBotX;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static java.lang.annotation.ElementType.*;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class QwoBotModule extends AbstractModule {
    private static final String CONFIG_FILE_PROPERTY = "config.file";
    private static final String DEFAULT_CONFIG_FILE = "qwobot.xml";

    @Override
    protected void configure() {
        setProperty("org.jboss.logging.provider", "slf4j");
        bind(HierarchicalConfiguration.class).toProvider(ConfigurationProvider.class).asEagerSingleton();
        bind(new TypeLiteral<org.pircbotx.Configuration<PircBotX>>(){}).toProvider(PircBotConfigurationProvider.class).asEagerSingleton();
        bindInterceptor(any(), annotatedWith(Subscribe.class), new EventBusExceptionHandler());
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

    @Provides
    @Singleton
    public SessionFactory sessionFactory() {
        final Configuration configuration = addEntities(hibernateConfiguration());
        return configuration.buildSessionFactory(new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build());
    }

    protected EventBus getEventBus() {
        return new AsyncEventBus(newFixedThreadPool(25));
    }

    private Configuration addEntities(Configuration configuration) {
        return configuration
                .addAnnotatedClass(ChatLog.class)
                .addAnnotatedClass(QwobotUser.class);
    }

    protected Configuration hibernateConfiguration() {
        return new Configuration()
                .setProperty("hibernate.connection.url", "jdbc:h2:datastores/qwobot")
                .setProperty("hibernate.connection.driver_class", Driver.class.getCanonicalName())
                .setProperty("hibernate.dialect", H2Dialect.class.getCanonicalName())
                .setProperty("hibernate.hbm2ddl.auto", "update");
    }

    @BindingAnnotation
    @Target({PARAMETER, METHOD, FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ConfigurationFilename {
    }

    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD)
    @interface RequiresAuthentication {}
}
