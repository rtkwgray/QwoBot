package com.sl5r0.qwobot.irc.service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.ChatLog;
import com.sl5r0.qwobot.persistence.SimpleRepository;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class LoggingService extends AbstractIrcEventService {
    private final SimpleRepository<ChatLog> chatLogRepository;

    @Inject
    protected LoggingService(SimpleRepository<ChatLog> chatLogRepository, EventBus eventBus) {
        super(eventBus);
        this.chatLogRepository = checkNotNull(chatLogRepository, "chatLogRepository must not be null");
    }

    @Subscribe
    public void createChatLog(MessageEvent<PircBotX> event) {
        doLog(event);
    }

    public void doLog(MessageEvent<PircBotX> event) {
        chatLogRepository.save(ChatLog.fromMessageEvent(event));
    }
}
