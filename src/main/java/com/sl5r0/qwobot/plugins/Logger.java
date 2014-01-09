package com.sl5r0.qwobot.plugins;

import com.google.common.eventbus.Subscribe;
import com.sl5r0.qwobot.api.QwoBotPlugin;
import com.sl5r0.qwobot.core.QwoBot;
import com.sl5r0.qwobot.domain.MessageEvent;

public class Logger extends QwoBotPlugin {
    public Logger(QwoBot qwoBot) {
        super(qwoBot);
    }

    @Override
    public String getDescription() {
        return "Simple logging plugin";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public String getHelp() {
        return "!otr - Off the record; messages starting with this command will not be logged.";
    }

    @Subscribe
    public void processMessage(MessageEvent messageEvent) {
        System.out.println(messageEvent);
    }
}
