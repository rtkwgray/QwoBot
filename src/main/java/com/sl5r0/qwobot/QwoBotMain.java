package com.sl5r0.qwobot;

import com.sl5r0.qwobot.core.QwoBotInternal;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.pircbotx.exception.IrcException;

import java.io.IOException;

public class QwoBotMain {
    private static final String CONFIGURATION_FILE = "config.xml";

    public static void main(String[] args) throws IOException, IrcException {
        QwoBotInternal qwoBot = new QwoBotInternal(loadConfigurationFromFile(CONFIGURATION_FILE));
        qwoBot.start();
    }

    public static HierarchicalConfiguration loadConfigurationFromFile(String filename) {
        XMLConfiguration configuration = null;
        try {
            configuration = new XMLConfiguration(filename);
        } catch (ConfigurationException e) {
            System.err.println("Could not read " + filename + ": " + e.getLocalizedMessage());
            System.err.println("Please ensure the file exists and that the bot has access to it.");
            System.exit(1);
        }
        return configuration;
    }
}
