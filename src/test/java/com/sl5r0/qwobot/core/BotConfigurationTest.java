package com.sl5r0.qwobot.core;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import java.io.File;

public class BotConfigurationTest {
    @Test (expected = ConfigurationException.class)
    public void ensureExceptionIsThrownWhenConfigFileDoesNotExist() throws Exception {
        new BotConfiguration(new File("doesNotExist"));
    }
}
