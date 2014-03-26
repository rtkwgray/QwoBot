package com.sl5r0.qwobot;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.sl5r0.qwobot.core.QwoBotModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QwoBot {
    private static final Logger log = LoggerFactory.getLogger(QwoBot.class);
//    private final com.sl5r0.qwobot.core.QwoBot qwoBot;

//    @VisibleForTesting
//    QwoBot(Injector guice) {
//        qwoBot = guice.getInstance(com.sl5r0.qwobot.core.QwoBot.class);
//    }

    @VisibleForTesting
    void initialize() throws Exception {
        try {
//            qwoBot.start();
        } catch (Exception e) {
            log.error("Something went wrong during startup", e);
        }
    }

    @Inject
    private QwoBot(Provider<org.pircbotx.Configuration> config) {
        System.out.println(config.get());
//        super(new org.pircbotx.Configuration.Builder().buildConfiguration());

//        Configuration config = new HierarchicalConfiguration();
//        config.setProperty("something.otherthing", "What");
//        Iterator<String> keys = config.getKeys();
//        while(keys.hasNext()) {
//            System.out.println(keys.next());
//        }
//        System.out.println(config.getProperties(null));
    }

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new QwoBotModule("qwobot"));
        injector.getInstance(QwoBot.class);
//        new QwoBot(new HierarchicalConfiguration());
    }


}