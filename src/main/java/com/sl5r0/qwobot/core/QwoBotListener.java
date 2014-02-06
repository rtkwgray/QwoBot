package com.sl5r0.qwobot.core;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import static com.google.common.base.Preconditions.checkNotNull;

public class QwoBotListener extends ListenerAdapter<QwoBot> {
    private final EventBus eventBus;

    @Inject
    public QwoBotListener(EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus, "eventBus cannot be null");
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        eventBus.post(event);
    }
}
