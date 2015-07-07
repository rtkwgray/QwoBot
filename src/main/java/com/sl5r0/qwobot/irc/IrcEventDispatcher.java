package com.sl5r0.qwobot.irc;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class IrcEventDispatcher extends ListenerAdapter<PircBotX> {
    private static final Logger log = getLogger(IrcEventDispatcher.class);
    private final ExecutorService executor = newFixedThreadPool(50);
    private final EventBus eventBus;

    @Inject
    public IrcEventDispatcher(EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus, "eventBus cannot be null");
    }

    @Override
    public void onEvent(final Event<PircBotX> event) throws Exception {
        super.onEvent(event);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                eventBus.post(event);
            }
        });
    }
}
