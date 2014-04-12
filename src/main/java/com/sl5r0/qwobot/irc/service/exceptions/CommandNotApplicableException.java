package com.sl5r0.qwobot.irc.service.exceptions;

public class CommandNotApplicableException extends RuntimeException {
    public CommandNotApplicableException(String message) {
        super(message);
    }
}
