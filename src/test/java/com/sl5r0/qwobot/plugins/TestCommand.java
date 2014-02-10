package com.sl5r0.qwobot.plugins;

import com.sl5r0.qwobot.plugins.commands.Command;

public class TestCommand implements Command {
    private final String help;

    public TestCommand(String help) {
        this.help = help;
    }

    @Override
    public String getHelp() {
        return help;
    }
}
