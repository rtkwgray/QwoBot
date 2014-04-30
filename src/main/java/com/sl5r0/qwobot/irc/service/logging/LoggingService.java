package com.sl5r0.qwobot.irc.service.logging;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.ChatLog;
import com.sl5r0.qwobot.irc.command.CommandHandler;
import com.sl5r0.qwobot.irc.service.AbstractIrcEventService;
import com.sl5r0.qwobot.persistence.SimpleRepository;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sl5r0.qwobot.irc.command.Command.forEvent;

@Singleton
public class LoggingService extends AbstractIrcEventService {
    private final SimpleRepository<ChatLog> chatLogRepository;

    @Inject
    protected LoggingService(SimpleRepository<ChatLog> chatLogRepository) {
        this.chatLogRepository = checkNotNull(chatLogRepository, "chatLogRepository must not be null");
    }

    public void createChatLog(MessageEvent event) {
        chatLogRepository.save(ChatLog.fromMessageEvent(event));
    }

    @Override
    protected void initialize() {
        registerCommand(
                forEvent(MessageEvent.class)
                        .description("Create a chat log")
                        .handler(new CommandHandler<MessageEvent>() {
                            @Override
                            public void handle(MessageEvent event, List<String> arguments) {
                                createChatLog(event);
                            }
                        })
                        .build()
        );
    }
}
