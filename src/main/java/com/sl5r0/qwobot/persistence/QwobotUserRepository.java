package com.sl5r0.qwobot.persistence;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.QwobotUser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

@Singleton
public class QwobotUserRepository extends SimpleRepository<QwobotUser> {
    @Inject
    public QwobotUserRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
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

    public List<QwobotUser> findRichest(final int limit) {
        return new DatabaseOperation<List<QwobotUser>>(sessionFactory) {
            @SuppressWarnings("unchecked")
            @Override
            protected List<QwobotUser> doExecute(Session session) {
                return session.createQuery("from QwobotUser user order by user.balance desc").setMaxResults(limit).list();
            }
        }.execute().get();
    }
}
