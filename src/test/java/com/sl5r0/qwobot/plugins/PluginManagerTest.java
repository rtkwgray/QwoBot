package com.sl5r0.qwobot.plugins;

import com.google.common.eventbus.EventBus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static com.sl5r0.qwobot.core.TestModule.testInjector;
import static com.sl5r0.qwobot.plugins.AnotherTestPlugin.COMMAND;
import static com.sl5r0.qwobot.plugins.TestPlugin.COMMAND_1;
import static com.sl5r0.qwobot.plugins.TestPlugin.COMMAND_2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PluginManagerTest {
    private final EventBus eventBus = mock(EventBus.class);
    private PluginManager pluginManager;

    @Before
    public void setUp() throws Exception {
        pluginManager = testInjector()
                .withConfiguration("qwobot-pluginManager.xml")
                .withEventBus(eventBus)
                .instanceOf(PluginManager.class);
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
//        Injector injector = Guice.createInjector(new TestModule());
//        SessionFactory sessionFactory = injector.getInstance(SessionFactoryCreator.class).sessionFactoryFor("testdb", "com.sl5r0.qwobot.plugins.qbux");
//        Session session = sessionFactory.openSession();
//        session.beginTransaction();
//        session.save(new User());
//        session.save(new User());
//        session.save(new User());
//        session.getTransaction().commit();
//
//        List list = session.createCriteria(User.class).list();
//        System.out.println(1);
//        System.out.println(list);
//
//        sessionFactory.close();
//        Configuration configuration = new Configuration();
//        configuration = configuration.setProperty("hibernate.connection.url", "jdbc:h2:datastores/test2");
//        configuration = configuration.setProperty("hibernate.connection.driver_class", Driver.class.getCanonicalName());
//        configuration = configuration.setProperty("hibernate.dialect", H2Dialect.class.getCanonicalName());
//        configuration = configuration.setProperty("hibernate.hbm2ddl.auto", "update"); // create schema if it doesn't exist
//        configuration = configuration.addAnnotatedClass(User.class);
//        StandardServiceRegistryBuilder bootstrapServiceRegistryBuilder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
//        SessionFactory sessionFactory = configuration.buildSessionFactory(bootstrapServiceRegistryBuilder.build());
//
//
//

    }
}