package com.sl5r0.qwobot.plugins;

import com.google.common.collect.ImmutableSet;
import com.sl5r0.qwobot.plugins.commands.Command;

import java.util.Set;

public class TestPlugin extends Plugin {
    public static final Command COMMAND_1 = new TestCommand("1");
    public static final Command COMMAND_2 = new TestCommand("2");

    @Override
    public Set<Command> getCommands() {
        return ImmutableSet.<Command>builder().add(COMMAND_1).add(COMMAND_2).build();
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
}
