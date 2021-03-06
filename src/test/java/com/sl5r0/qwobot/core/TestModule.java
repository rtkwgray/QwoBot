package com.sl5r0.qwobot.core;

import com.sl5r0.qwobot.guice.QwoBotModule;
import org.hibernate.cfg.Configuration;

public class TestModule extends QwoBotModule {
    @Override
    protected Configuration hibernateConfiguration() {
        return super.hibernateConfiguration()
                .setProperty("hibernate.connection.url", "jdbc:h2:datastores/test")
                .setProperty("hibernate.hbm2ddl.auto", "create");
    }
}
