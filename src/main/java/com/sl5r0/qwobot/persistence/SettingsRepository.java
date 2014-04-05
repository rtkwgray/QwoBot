package com.sl5r0.qwobot.persistence;

import com.google.inject.Inject;
import com.sl5r0.qwobot.domain.PersistedSetting;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class SettingsRepository {
    private final SessionFactory sessionFactory;

    @Inject
    public SettingsRepository(SessionFactory sessionFactory) {
        this.sessionFactory = checkNotNull(sessionFactory, "sessionFactory must not be null");
    }

    public PersistedSetting save(final PersistedSetting setting) {
        return new DatabaseOperation<PersistedSetting>(sessionFactory) {
            @Override
            protected PersistedSetting doExecute(Session session) {
                session.saveOrUpdate(setting);
                return setting;
            }
        }.execute().get();
    }
}
