package com.sl5r0.qwobot.core;

import org.hibernate.cfg.Configuration;

public class TestModule extends QwobotModule {
    @Override
    protected Configuration hibernateConfiguration() {
        return super.hibernateConfiguration()
                .setProperty("hibernate.connection.url", "jdbc:h2:datastores/test")
                .setProperty("hibernate.hbm2ddl.auto", "create");
    }
}
