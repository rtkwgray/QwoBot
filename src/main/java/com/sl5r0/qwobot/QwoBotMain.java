package com.sl5r0.qwobot;

import com.google.api.client.repackaged.com.google.common.annotations.VisibleForTesting;
import com.google.inject.Injector;
import com.sl5r0.qwobot.core.QwoBot;
import com.sl5r0.qwobot.core.QwoBotModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.inject.Guice.createInjector;

public class QwoBotMain {
    private static final Logger log = LoggerFactory.getLogger(QwoBotMain.class);
    private final QwoBot qwoBot;

    @VisibleForTesting
    QwoBotMain(Injector guice) {
        qwoBot = guice.getInstance(QwoBot.class);
    }

    @VisibleForTesting
    void initialize() throws Exception {
        try {
            qwoBot.start();
        } catch (Exception e) {
            log.error("Something went wrong during startup", e);
        }
    }

    public static void main(String[] args) throws Exception {
        new QwoBotMain(createInjector(new QwoBotModule())).initialize();
    }
}