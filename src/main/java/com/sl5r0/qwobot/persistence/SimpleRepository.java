package com.sl5r0.qwobot.persistence;

import com.google.inject.Inject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class SimpleRepository<T> {
    protected final SessionFactory sessionFactory;

    @Inject
    public SimpleRepository(SessionFactory sessionFactory) {
        this.sessionFactory = checkNotNull(sessionFactory, "sessionFactory must not be null");
    }

    public T save(final T object) {
        return new DatabaseOperation<T>(sessionFactory) {
            @Override
            protected T doExecute(Session session) {
                final Transaction transaction = session.beginTransaction();
                session.save(object);
                transaction.commit();
                return object;
            }
        }.execute().get();
    }

    public T saveOrUpdate(final T object) {
        return new DatabaseOperation<T>(sessionFactory) {
            @Override
            protected T doExecute(Session session) {
                final Transaction transaction = session.beginTransaction();
                session.saveOrUpdate(object);
                transaction.commit();
                return object;
            }
        }.execute().get();
    }

    public T delete(final T object) {
        return new DatabaseOperation<T>(sessionFactory) {
            @Override
            protected T doExecute(Session session) {
                final Transaction transaction = session.beginTransaction();
                session.delete(object);
                transaction.commit();
                return object;
            }
        }.execute().get();
    }

    public List<T> findAll(final Class<T> entityType) {
        return new DatabaseOperation<List<T>>(sessionFactory) {
            @Override
            protected List<T> doExecute(Session session) {
                //noinspection unchecked
                return session.createCriteria(entityType).list();
            }
        }.execute().get();
    }
}