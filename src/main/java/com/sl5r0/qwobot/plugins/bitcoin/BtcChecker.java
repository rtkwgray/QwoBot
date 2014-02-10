package com.sl5r0.qwobot.plugins.bitcoin;

import com.google.common.collect.ImmutableSet;
import com.sl5r0.qwobot.plugins.Plugin;
import com.sl5r0.qwobot.plugins.commands.Command;

import java.util.Set;

public class BtcChecker extends Plugin {
    private static final Set<Command> COMMANDS = ImmutableSet.<Command>of(new FetchBitcoinPrices());

    @Override
    public Set<Command> getCommands() {
        return COMMANDS;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
