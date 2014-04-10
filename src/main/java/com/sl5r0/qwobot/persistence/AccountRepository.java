package com.sl5r0.qwobot.persistence;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.Account;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

@Singleton
public class AccountRepository extends SimpleRepository<Account> {
    @Inject
    public AccountRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Account> findByNick(final String username) {
        return new DatabaseOperation<Account>(sessionFactory) {
            @Override
            protected Account doExecute(Session session) {
                return (Account) session.createQuery("from Account user where user.username = :username")
                        .setString("username", username).uniqueResult();
            }
        }.execute();
    }

    public Optional<Account> findById(final long id) {
        return new DatabaseOperation<Account>(sessionFactory) {
            @Override
            protected Account doExecute(Session session) {
                return (Account) session.createQuery("from Account user where user.id = :id")
                        .setLong("id", id).uniqueResult();
            }
        }.execute();
    }

    public List<Account> findRichest(final int limit) {
        return new DatabaseOperation<List<Account>>(sessionFactory) {
            @SuppressWarnings("unchecked")
            @Override
            protected List<Account> doExecute(Session session) {
                return session.createQuery("from Account user order by user.balance desc").setMaxResults(limit).list();
            }
        }.execute().get();
    }

    public Optional<Account> findByUsernamePassword(final String username, final String password) {
        return new DatabaseOperation<Account>(sessionFactory) {
            @Override
            protected Account doExecute(Session session) {
                return (Account) session.createQuery("from Account user where user.password = :password and user.username = :username")
                        .setString("password", password)
                        .setString("username", username).uniqueResult();
            }
        }.execute();
    }
}
