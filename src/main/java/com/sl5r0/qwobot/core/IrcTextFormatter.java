package com.sl5r0.qwobot.core;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import org.pircbotx.Colors;

import java.util.List;
import java.util.Queue;

import static com.google.common.collect.Queues.newArrayDeque;

/**
 * Simply an enum to simplify use of the PircBotX Colors class.
 */
@SuppressWarnings("UnusedDeclaration") // Don't care if they're not used.
public enum IrcTextFormatter {
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
    LIGHT_GRAY(Colors.LIGHT_GRAY),
    RAINBOW(ImmutableList.of(Colors.PURPLE, Colors.BLUE, Colors.CYAN, Colors.GREEN, Colors.YELLOW, Colors.RED)),
    FIRE(ImmutableList.of(Colors.YELLOW, Colors.RED)),
    ICE(ImmutableList.of(Colors.BLUE, Colors.WHITE, Colors.CYAN, Colors.WHITE));

    private final Function<String, String> formatter;

    public static IrcTextFormatter fromString(String color) {
        final String enumValue = color.toUpperCase();
        return valueOf(enumValue);
    }

    public String format(String string) {
        return formatter.apply(string);
    }

    public static String format(boolean bool) {
        if (bool) {
            return GREEN.format("YES");
        } else {
            return RED.format("NO");
        }
    }

    private IrcTextFormatter(String ircCode) {
        this.formatter = applySingleColor(ircCode);
    }

    private IrcTextFormatter(List<String> colors) {
        this.formatter = applyMultipleColors(colors);
    }

    private static Function<String, String> applyMultipleColors(final List<String> colors) {
        return new Function<String, String>() {
            @Override
            public String apply(String input) {
                final Queue<String> colorQueue = newArrayDeque(colors);
                final StringBuilder colorized = new StringBuilder(input.length() * 2);
                for (char currentCharacter : input.toCharArray()) {
                    final String color = colorQueue.remove();
                    colorQueue.add(color);
                    colorized.append(color).append(currentCharacter);
                }
                colorized.append(Colors.NORMAL);
                return colorized.toString();
            }
        };
    }

    private static Function<String, String> applySingleColor(final String color) {
        return new Function<String, String>() {
            @Override
            public String apply(String input) {
                return color + input + Colors.NORMAL;
            }
        };
    }
}
