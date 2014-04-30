package com.sl5r0.qwobot.irc.command;

import com.google.common.base.Function;
import com.sl5r0.qwobot.irc.IrcTextFormatter;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.sl5r0.qwobot.irc.command.ParameterType.*;
import static java.util.regex.Pattern.compile;

public class Parameter {
    public static final Pattern urlPattern = compile("(https?://\\S+\\.\\S+)");
    private final ParameterType type;
    private final String description;
    private boolean optional = false;
    private boolean repeating = false;
    private boolean positional = true;

    private Parameter(ParameterType type, String description) {
        this.type = type;
        this.description = description;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean repeats() {
        return repeating;
    }

    public boolean isPositional() {
        return positional;
    }

    public ParameterType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public static Parameter integer(String description) {
        return new Parameter(INTEGER, description);
    }

    public static Parameter literal(String literal) {
        return new Parameter(LITERAL, literal);
    }

    public static Parameter string(String description) {
        return new Parameter(STRING, description);
    }

    public static Parameter url() {
        return new Parameter(URL, "url");
    }

    public static Parameter optional(Parameter parameter) {
        parameter.optional = true;
        return parameter;
    }

    public static Parameter repeating(Parameter parameter) {
        parameter.repeating = true;
        return parameter;
    }

    public static Parameter anywhere(Parameter parameter) {
        checkArgument(parameter.type == URL, "only URL parameters can be anywhere");
        parameter.positional = false;
        parameter.repeating = true;
        return parameter;
    }

    public static Function<Parameter, String> toFormattedString = new Function<Parameter, String>() {
        @Override
        public String apply(Parameter input) {
            String formattedString;
            if (input.type == LITERAL) {
                formattedString = input.getDescription();
            } else if(input.isOptional()) {
                formattedString = "[" + input.getDescription() + "]";
            } else {
                formattedString = "<" + input.getDescription() + ">";
            }

            if (input.repeats()) {
                formattedString += " ... " + formattedString;
            }

            switch (input.getType()) {
                case LITERAL:
                    return IrcTextFormatter.GREEN.format(formattedString);
                default:
                    return IrcTextFormatter.CYAN.format(formattedString);
            }
        }
    };
}
