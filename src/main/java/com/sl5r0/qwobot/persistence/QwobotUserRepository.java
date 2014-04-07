package com.sl5r0.qwobot.persistence;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.QwobotUser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

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
}
