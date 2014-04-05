package com.sl5r0.qwobot.persistence;

import com.google.inject.Inject;
import com.sl5r0.qwobot.domain.ChatLog;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import static com.google.common.base.Preconditions.checkNotNull;

public class ChatLogRepository {
    private final SessionFactory sessionFactory;

    @Inject
    public ChatLogRepository(SessionFactory sessionFactory) {
        this.sessionFactory = checkNotNull(sessionFactory, "sessionFactory must not be null");
    }

    public ChatLog save(final ChatLog chatLog) {
        return new DatabaseOperation<ChatLog>(sessionFactory) {
            @Override
            protected ChatLog doExecute(Session session) {
                final Transaction transaction = session.beginTransaction();
                session.save(chatLog);
                transaction.commit();
                return chatLog;
            }
        }.execute().get();
    }

}
