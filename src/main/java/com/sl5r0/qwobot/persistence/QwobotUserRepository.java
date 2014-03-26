package com.sl5r0.qwobot.persistence;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.sl5r0.qwobot.domain.QwobotUser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class QwobotUserRepository {
    private final SessionFactory sessionFactory;

    @Inject
    public QwobotUserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = checkNotNull(sessionFactory, "sessionFactory must not be null");
    }

    public void save(final QwobotUser oldQwobotUser) {
        new DatabaseOperation<QwobotUser>(sessionFactory) {
            @Override
            protected QwobotUser doExecute(Session session) {
                final Transaction transaction = session.beginTransaction();
                session.save(oldQwobotUser);
                transaction.commit();
                return oldQwobotUser;
            }
        }.execute();
    }

    public Optional<QwobotUser> findByHostMask(final String hostMask) {
        return new DatabaseOperation<QwobotUser>(sessionFactory) {
            @Override
            protected QwobotUser doExecute(Session session) {
                return (QwobotUser) session.createQuery("from QwobotUser user where user.hostMask = :hostMask")
                        .setString("hostMask", hostMask).uniqueResult();
            }
        }.execute();
    }

    public Optional<QwobotUser> findByAuthenticationToken(final UUID authenticationToken) {
        return new DatabaseOperation<QwobotUser>(sessionFactory) {
            @Override
            protected QwobotUser doExecute(Session session) {
                return (QwobotUser) session.createQuery("from QwobotUser user where user.authenticationToken = :authenticationToken")
                        .setParameter("authenticationToken", authenticationToken).uniqueResult();
            }
        }.execute();
    }
}
