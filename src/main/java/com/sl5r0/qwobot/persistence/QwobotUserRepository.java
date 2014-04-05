package com.sl5r0.qwobot.persistence;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.QwobotUser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class QwobotUserRepository {
    private final SessionFactory sessionFactory;

    @Inject
    public QwobotUserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = checkNotNull(sessionFactory, "sessionFactory must not be null");
    }

    public QwobotUser save(final QwobotUser oldQwobotUser) {
        return new DatabaseOperation<QwobotUser>(sessionFactory) {
            @Override
            protected QwobotUser doExecute(Session session) {
                final Transaction transaction = session.beginTransaction();
                session.saveOrUpdate(oldQwobotUser);
                transaction.commit();
                return oldQwobotUser;
            }
        }.execute().get();
    }

    public List<QwobotUser> allUsers() {
        return new DatabaseOperation<List<QwobotUser>>(sessionFactory) {
            @Override
            protected List<QwobotUser> doExecute(Session session) {
                //noinspection unchecked
                return session.createQuery("from QwobotUser").list();
            }
        }.execute().get();
    }

    public Optional<QwobotUser> findByNick(final String nick) {
        return new DatabaseOperation<QwobotUser>(sessionFactory) {
            @Override
            protected QwobotUser doExecute(Session session) {
                return (QwobotUser) session.createQuery("from QwobotUser user where user.nick = :nick")
                        .setString("nick", nick).uniqueResult();
            }
        }.execute();
    }
}
