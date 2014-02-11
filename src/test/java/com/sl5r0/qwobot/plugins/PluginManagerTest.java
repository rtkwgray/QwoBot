package com.sl5r0.qwobot.plugins;

import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sl5r0.qwobot.core.BotConfiguration;
import com.sl5r0.qwobot.plugins.qbux.TestEntity;
import org.h2.Driver;
import org.hamcrest.Matchers;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.sl5r0.qwobot.helpers.UnitTestHelpers.configFromResource;
import static com.sl5r0.qwobot.plugins.AnotherTestPlugin.COMMAND;
import static com.sl5r0.qwobot.plugins.TestPlugin.COMMAND_1;
import static com.sl5r0.qwobot.plugins.TestPlugin.COMMAND_2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PluginManagerTest {
    private final Injector injector = Guice.createInjector(new TestModule());
    private final EventBus eventBus = mock(EventBus.class);
    private PluginManager pluginManager;

    @Before
    public void setUp() throws Exception {
        final BotConfiguration botConfiguration = configFromResource("qwobot-pluginManager.xml");
        pluginManager = new PluginManager(injector, eventBus, new PluginClassResolver(botConfiguration));
    }

    @Test
    public void ensurePluginsAreOnlyLoadedOnce() throws Exception {
        assertThat(pluginManager.getRegisteredPlugins(), hasSize(2));

        //noinspection unchecked
        assertThat(pluginManager.getRegisteredPlugins(), containsInAnyOrder(
                Matchers.instanceOf(TestPlugin.class),
                Matchers.instanceOf(AnotherTestPlugin.class)
        ));
    }

    @Test
    public void ensureAllPluginCommandsAreRegistered() throws Exception {
        verify(eventBus).register(COMMAND_1);
        verify(eventBus).register(COMMAND_2);
        verify(eventBus).register(COMMAND);
    }

    @Test
    public void ensureGetCommandsForPluginReturnsCorrectCommands() throws Exception {
        assertThat(pluginManager.getCommandsForPlugin("TestPlugin"), containsInAnyOrder(COMMAND_1, COMMAND_2));
        assertThat(pluginManager.getCommandsForPlugin("AnotherTestPlugin"), contains(COMMAND));
    }

    @Test
    public void testName() throws Exception {
        Configuration configuration = new Configuration();
        configuration = configuration.setProperty("hibernate.connection.url", "jdbc:h2:datastores/test2");
        configuration = configuration.setProperty("hibernate.connection.driver_class", Driver.class.getCanonicalName());
        configuration = configuration.setProperty("hibernate.dialect", H2Dialect.class.getCanonicalName());
        configuration = configuration.setProperty("hibernate.hbm2ddl.auto", "update"); // create schema if it doesn't exist
        configuration = configuration.addAnnotatedClass(TestEntity.class);
        StandardServiceRegistryBuilder bootstrapServiceRegistryBuilder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        SessionFactory sessionFactory = configuration.buildSessionFactory(bootstrapServiceRegistryBuilder.build());



        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(new TestEntity());
        session.save(new TestEntity());
        session.save(new TestEntity());
        session.getTransaction().commit();

        List list = session.createCriteria(TestEntity.class).list();
        System.out.println(list);
    }
}