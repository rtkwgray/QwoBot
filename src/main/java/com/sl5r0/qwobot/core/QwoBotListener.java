package com.sl5r0.qwobot.core;

import com.google.common.eventbus.EventBus;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class QwoBotListener extends ListenerAdapter {
    private final EventBus eventBus;

    public QwoBotListener(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        eventBus.post(event);
    }
}
