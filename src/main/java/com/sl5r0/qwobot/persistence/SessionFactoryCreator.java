package com.sl5r0.qwobot.persistence;

import org.h2.Driver;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;
import org.reflections.Reflections;

import javax.persistence.Entity;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class SessionFactoryCreator {
    private final String databasePrefix;
    private final SchemaRule schemaRule;

    public SessionFactoryCreator(String databasePrefix, SchemaRule schemaRule) {
        this.databasePrefix = checkNotNull(databasePrefix);
        this.schemaRule = checkNotNull(schemaRule);
    }

    public SessionFactory sessionFactoryFor(String databaseName, Package entityBasePackage) {
        final Reflections classScanner = new Reflections(entityBasePackage.getName());
        final Set<Class<?>> entityClasses = classScanner.getTypesAnnotatedWith(Entity.class);

        final Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.url", "jdbc:h2:datastores/" + databasePrefix + databaseName)
                .setProperty("hibernate.connection.driver_class", Driver.class.getCanonicalName())
                .setProperty("hibernate.dialect", H2Dialect.class.getCanonicalName())
                .setProperty("hibernate.hbm2ddl.auto", schemaRule.propertyValue); // create schema if it doesn't exist

        for (Class<?> entityClass : entityClasses) {
            configuration.addAnnotatedClass(entityClass);
        }

        final StandardServiceRegistryBuilder bootstrapServiceRegistryBuilder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        return configuration.buildSessionFactory(bootstrapServiceRegistryBuilder.build());
    }

    public static enum SchemaRule {
        CREATE("create"),
        UPDATE("update");

        private final String propertyValue;

        private SchemaRule(String propertyValue) {
            this.propertyValue = propertyValue;
        }
    }
}
