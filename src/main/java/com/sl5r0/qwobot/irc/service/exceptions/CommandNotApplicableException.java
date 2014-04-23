package com.sl5r0.qwobot.irc.service.exceptions;

import com.sl5r0.qwobot.domain.command.Command;

public class CommandNotApplicableException extends RuntimeException {
    private final Command command;
    public CommandNotApplicableException(String message, Command command) {
        super(message);
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }
}
