package com.sl5r0.qwobot.plugins.qbux.commands;

import com.sl5r0.qwobot.plugins.commands.TriggerCommand;
import com.sl5r0.qwobot.plugins.qbux.entities.OldQwobotUser;
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
    private static final int INITIAL_BALANCE = 0;
    private final SessionFactory sessionFactory;

    public Register(SessionFactory sessionFactory) {
        super(TRIGGER);
        this.sessionFactory = checkNotNull(sessionFactory, "sessionFactory cannot be null");
    }

    @Override
    public void triggered(MessageEvent event) {
        final String nick = event.getUser().getNick();
        log.info("Registering oldQwobotUser: " + event.getUser().getNick());

        final OldQwobotUser oldQwobotUser = new OldQwobotUser();
        oldQwobotUser.setNick(event.getUser().getNick());
        oldQwobotUser.setBalance(INITIAL_BALANCE);

        final Session session = sessionFactory.openSession();
        try {
            Transaction transaction = session.beginTransaction();
            session.save(oldQwobotUser);
            try {
                transaction.commit();
                log.info("Successfully registered oldQwobotUser: " + nick);
                event.getChannel().send().message("Welcome to QBux! Here's " + oldQwobotUser.getBalance() + " QBUX to get you started.");
            } catch (ConstraintViolationException e) {
                event.getChannel().send().message("It looks like you already have an oldQwobotUser.");
                log.info("OldQwobotUser already registered: " + nick);
            } catch (RuntimeException e) {
                event.getChannel().send().message("Sorry, something went wrong. Try again later!");
                log.warn("Failed to create oldQwobotUser " + nick, e);
            }
        } finally {
            session.close();
        }
    }
}
