package com.sl5r0.qwobot.plugins;

import com.google.common.collect.ImmutableSet;
import com.sl5r0.qwobot.plugins.commands.Command;

import java.util.Set;

public class AnotherTestPlugin extends Plugin {
    public static final Command COMMAND = new TestCommand("3");

    @Override
    public Set<Command> getCommands() {
        return ImmutableSet.<Command>builder().add(COMMAND).build();
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
}
