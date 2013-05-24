package com.sl5r0.qwobot.core;

import com.google.common.eventbus.EventBus;
import com.sl5r0.qwobot.domain.MessageEvent;
import org.pircbotx.hooks.ListenerAdapter;

public class QwoBotListener extends ListenerAdapter {
    private final EventBus eventBus;

    public QwoBotListener(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onMessage(org.pircbotx.hooks.events.MessageEvent event) throws Exception {
        eventBus.post(new MessageEvent(event));
    }
}
