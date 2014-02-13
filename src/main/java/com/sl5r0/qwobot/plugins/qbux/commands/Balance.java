package com.sl5r0.qwobot.plugins.qbux.commands;

import com.sl5r0.qwobot.plugins.commands.TriggerCommand;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class Balance extends TriggerCommand {
    private static final String TRIGGER = "balance";
    private final SessionFactory sessionFactory;

    public Balance(SessionFactory sessionFactory) {
        super(TRIGGER);
        this.sessionFactory = checkNotNull(sessionFactory, "sessionFactory cannot be null");
    }

    @Override
    public void triggered(MessageEvent event) {
        Session session = sessionFactory.openSession();
        List user = session.createQuery("select balance from User user where user.nick = :nick").setString("nick", event.getUser().getNick()).list();
        for (Object o : user) {
            System.out.println(o.toString());
        }
    }
}