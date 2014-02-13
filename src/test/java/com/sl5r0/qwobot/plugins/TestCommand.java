package com.sl5r0.qwobot.plugins;

import com.sl5r0.qwobot.plugins.commands.Command;

import java.util.List;

import static java.util.Collections.singletonList;

public class TestCommand implements Command {
    private final String help;

    public TestCommand(String help) {
        this.help = help;
    }

    @Override
    public List<String> getHelp() {
        return singletonList(help);
    }
}
