package com.sl5r0.qwobot.plugins.qbux.commands;

import com.google.common.base.Optional;
import com.sl5r0.qwobot.plugins.commands.TriggerCommand;
import com.sl5r0.qwobot.plugins.qbux.entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pircbotx.hooks.events.MessageEvent;

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
        final Session session = sessionFactory.openSession();
        try {
            final String nick = event.getUser().getNick();
            final Optional<User> user = User.findByNick(nick, session);
            if (user.isPresent()) {
                event.getChannel().send().message("Current balance for " + nick + ": " + user.get().getBalance() + " QBUX");
            } else {
                event.getChannel().send().message("I don't have any record for " + nick + ". Are they registered?");
            }
        } finally {
            session.close();
        }
    }
}