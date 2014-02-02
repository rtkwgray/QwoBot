package com.sl5r0.qwobot.core;

import org.pircbotx.Colors;

/**
 * Simply an enum to simplify use of the PircBotX Colors class.
 */
@SuppressWarnings("UnusedDeclaration") // Don't care if they're not used.
public enum Format {
    NORMAL(Colors.NORMAL),
    BOLD(Colors.BOLD),
    UNDERLINE(Colors.UNDERLINE),
    REVERSE(Colors.REVERSE),
    WHITE(Colors.WHITE),
    BLACK(Colors.BLACK),
    DARK_BLUE(Colors.DARK_BLUE),
    RED(Colors.RED),
    BROWN(Colors.BROWN),
    PURPLE(Colors.PURPLE),
    OLIVE(Colors.OLIVE),
    YELLOW(Colors.YELLOW),
    GREEN(Colors.GREEN),
    TEAL(Colors.TEAL),
    CYAN(Colors.CYAN),
    BLUE(Colors.BLUE),
    MAGENTA(Colors.MAGENTA),
    DARK_GRAY(Colors.DARK_GRAY),
    LIGHT_GRAY(Colors.LIGHT_GRAY);

    private final String ircCode;

    public static Format fromString(String color) {
        final String enumValue = color.toUpperCase();
        return valueOf(enumValue);
    }

    public String getIrcCode() {
        return ircCode;
    }

    private Format(String ircCode) {
        this.ircCode = ircCode;
    }
}
