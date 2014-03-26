package com.sl5r0.qwobot.plugins.qbux.commands;

import com.google.common.base.Optional;
import com.sl5r0.qwobot.plugins.commands.ParameterizedTriggerCommand;
import com.sl5r0.qwobot.plugins.qbux.entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;
import java.util.Random;

import static com.sl5r0.qwobot.plugins.qbux.entities.User.findByNick;
import static java.lang.Math.*;

public class Mine extends ParameterizedTriggerCommand {
    private static final String TRIGGER = "mine";
    private final Random rng = new Random();
    private String currentAnswer;
    private String currentQuestion;
    private int difficulty = 1;
    private final SessionFactory sessionFactory;

    public Mine(SessionFactory sessionFactory) {
        super(TRIGGER);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void execute(MessageEvent event, List<String> parameters) {
        if (parameters.size() == 1) {
            if (parameters.get(0).equals(currentAnswer)) {
                event.getChannel().send().message(event.getUser().getNick() + " earns 1 QBUX! Network difficulty increasing!");
                difficulty++;
                updateUser(event);
                updateChannel(event);
            } else {
                event.getChannel().send().message("Invalid nonce. Try again.");
                difficulty = difficulty == 1 ? 1 : difficulty - 1;
            }
        } else {
            if (currentQuestion == null) {
                updateChannel(event);
            } else {
                event.getChannel().send().message("CURRENT BLOCK: " + currentQuestion);
            }
        }
    }

    private void updateChannel(MessageEvent event) {
        long multiplier = min(round(pow(10, difficulty)), 100000000);
        int num1 = rng.nextInt((int) multiplier);
        int num2 = rng.nextInt((int) multiplier);
        currentAnswer = Integer.toString(num1 + num2);
        currentQuestion = num1 + " + " + num2 + "?";

        event.getChannel().send().message("NEXT BLOCK: " + currentQuestion);
    }

    private void updateUser(MessageEvent event) {
        final Session session = sessionFactory.openSession();
        try {
            final Transaction transaction = session.beginTransaction();

            final Optional<User> toUser = findByNick(event.getUser().getNick(), session);
            if (!toUser.isPresent()) {
                event.getChannel().send().message("Oh, you don't seem to be registered. Let me just blow your QBUX away into digital oblivion.");
                return;
            }

            toUser.get().modifyBalance(1);

            session.save(toUser.get());
            transaction.commit();
        } catch (Exception e) {
            event.getChannel().send().message("Oops. They vanished into nothing. :S");
        } finally {
            session.close();
        }
    }
}
