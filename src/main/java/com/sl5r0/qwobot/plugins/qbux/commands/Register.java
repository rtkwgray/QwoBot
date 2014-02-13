package com.sl5r0.qwobot.plugins.qbux.commands;

import com.sl5r0.qwobot.plugins.commands.TriggerCommand;
import com.sl5r0.qwobot.plugins.qbux.entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class Register extends TriggerCommand {
    private static final Logger log = LoggerFactory.getLogger(Register.class);
    private static final String TRIGGER = "register";
    private final SessionFactory sessionFactory;

    public Register(SessionFactory sessionFactory) {
        super(TRIGGER);
        this.sessionFactory = checkNotNull(sessionFactory, "sessionFactory cannot be null");
    }

    @Override
    public void triggered(MessageEvent event) {
        final String nick = event.getUser().getNick();
        log.info("Registering user: " + event.getUser().getNick());

        final User user = new User();
        user.setNick(event.getUser().getNick());
        user.setBalance(100);

        final Session session = sessionFactory.openSession();
        try {
            Transaction transaction = session.beginTransaction();
            session.save(user);
            try {
                transaction.commit();
                log.info("Successfully registered user: " + nick);
            } catch (ConstraintViolationException e) {
                event.getChannel().send().message("It looks like you already have an account.");
                log.info("User already registered: " + nick);
            } catch (RuntimeException e) {
                event.getChannel().send().message("Sorry, something went wrong. Try again later!");
                log.warn("Failed to create user " + nick, e);
            }
        } finally {
            session.close();
        }
    }
}
