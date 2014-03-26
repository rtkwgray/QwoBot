package com.sl5r0.qwobot.persistence;

import com.google.common.base.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import static com.google.common.base.Optional.fromNullable;

/**
 * Helper class for wrapping database operations in a session.
 *
 * @param <T> The entity class to be returned from the query (may be void)
 */
public abstract class DatabaseOperation<T> {
    public final SessionFactory sessionFactory;

    public DatabaseOperation(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public final Optional<T> execute() {
        final Session session = sessionFactory.openSession();
        try {
            return fromNullable(doExecute(session));
        } finally {
            session.close();
        }
    }

    protected abstract T doExecute(Session session);
}
