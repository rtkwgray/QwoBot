package com.sl5r0.qwobot.plugins.qbux;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.sl5r0.qwobot.persistence.SessionFactoryCreator;
import com.sl5r0.qwobot.plugins.Plugin;
import com.sl5r0.qwobot.plugins.commands.Command;
import com.sl5r0.qwobot.plugins.commands.CompoundCommand;
import com.sl5r0.qwobot.plugins.commands.TriggerCommand;
import com.sl5r0.qwobot.plugins.qbux.commands.Balance;
import com.sl5r0.qwobot.plugins.qbux.commands.Register;
import com.sl5r0.qwobot.plugins.qbux.commands.Tip;
import org.hibernate.SessionFactory;

import java.util.Set;

public class QBuxCasino extends Plugin {
    private static final String QBUX_TRIGGER = "!qbux";
    private final Set<Command> commands;

    @Inject
    public QBuxCasino(SessionFactoryCreator sessionFactoryCreator) {
        final SessionFactory sessionFactory = sessionFactoryCreator.sessionFactoryFor("qbux", getClass().getPackage());
        commands = ImmutableSet.<Command>of(new CompoundCommand(QBUX_TRIGGER, ImmutableSet.<TriggerCommand>builder()
                .add(new Register(sessionFactory))
                .add(new Balance(sessionFactory))
                .add(new Tip(sessionFactory))
                .build()
        ));
    }

    @Override
    public Set<Command> getCommands() {
        return commands;
    }

    @Override
    public String getVersion() {
        return "0.1";
    }
}