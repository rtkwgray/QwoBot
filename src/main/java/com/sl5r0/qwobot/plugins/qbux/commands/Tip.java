package com.sl5r0.qwobot.plugins.qbux.commands;

import com.google.common.base.Optional;
import com.sl5r0.qwobot.plugins.commands.ParameterizedTriggerCommand;
import com.sl5r0.qwobot.plugins.exceptions.CommandExecutionException;
import com.sl5r0.qwobot.plugins.qbux.entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.sl5r0.qwobot.plugins.qbux.entities.User.findByNick;

public class Tip extends ParameterizedTriggerCommand {
    private static final Logger log = LoggerFactory.getLogger(Tip.class);
    private static final String TRIGGER = "tip";
    private final SessionFactory sessionFactory;

    public Tip(SessionFactory sessionFactory) {
        super(TRIGGER);
        this.sessionFactory = sessionFactory;
    }

    @Override // TODO: refactor this so that it's more meaningful. I'm just trying to get this done for now.
    public void execute(MessageEvent event, List<String> parameters) {
        if (parameters.size() < 2) {
            throw new CommandExecutionException("Invalid number of arguments.");
        }

        final long tipAmount;
        try {
            tipAmount = Long.valueOf(parameters.get(1));
        } catch (NumberFormatException e) {
            event.getChannel().send().message("That doesn't look like a valid number to me....");
            return;
        }

        if (tipAmount < 0) {
            event.getChannel().send().message("Theif!");
            return;
        } else if (tipAmount == 0) {
            event.getChannel().send().message("How generous....");
            return;
        }

        final String from = event.getUser().getNick();
        final String to = parameters.get(0);
        final Session session = sessionFactory.openSession();

        try {
            final Transaction transaction = session.beginTransaction();
            final Optional<User> fromUser = findByNick(from, session);
            if (!fromUser.isPresent()) {
                event.getChannel().send().message("Couldn't find you, " + from);
                return;
            }

            if (fromUser.get().getBalance() < tipAmount) {
                event.getChannel().send().message("You don't have enough QBUX.");
                return;
            }

            final Optional<User> toUser = findByNick(to, session);
            if (!toUser.isPresent()) {
                event.getChannel().send().message("I don't know who you're trying to tip. Have they registered?");
                return;
            }

            toUser.get().modifyBalance(tipAmount);
            fromUser.get().modifyBalance(-tipAmount);

            session.save(toUser.get());
            session.save(fromUser.get());
            transaction.commit();
            event.getChannel().send().message(from + " tipped " + to + " " + tipAmount + " QBUX! Good show!");
        } catch (Exception e) {
            event.getChannel().send().message("Tipping failed for some reason :S");
            log.error("Couldn't send tip from " + from + " to " + to, e);
        } finally {
            session.close();
        }
    }
}
