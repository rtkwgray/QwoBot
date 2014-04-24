package com.sl5r0.qwobot.domain.command;

import com.google.common.base.Function;
import com.sl5r0.qwobot.core.IrcTextFormatter;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.sl5r0.qwobot.domain.command.ParameterType.*;
import static java.util.regex.Pattern.compile;

public class NewParameter {
    public static final Pattern urlPattern = compile("(https?://\\S+\\.\\S+)");
    private final ParameterType type;
    private final String description;
    private boolean optional = false;
    private boolean repeating = false;
    private boolean positional = true;

    private NewParameter(ParameterType type, String description) {
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

    public static NewParameter integer(String description) {
        return new NewParameter(INTEGER, description);
    }

    public static NewParameter literal(String literal) {
        return new NewParameter(LITERAL, literal);
    }

    public static NewParameter string(String description) {
        return new NewParameter(STRING, description);
    }

    public static NewParameter url() {
        return new NewParameter(URL, "url");
    }

    public static NewParameter optional(NewParameter parameter) {
        parameter.optional = true;
        return parameter;
    }

    public static NewParameter repeating(NewParameter parameter) {
        parameter.repeating = true;
        return parameter;
    }

    public static NewParameter anywhere(NewParameter parameter) {
        checkArgument(parameter.type == URL, "only URL parameters can be anywhere");
        parameter.positional = false;
        parameter.repeating = true;
        return parameter;
    }

    public static Function<NewParameter, String> toFormattedString = new Function<NewParameter, String>() {
        @Override
        public String apply(NewParameter input) {
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
                    return IrcTextFormatter.WHITE.format(formattedString);
                default:
                    return IrcTextFormatter.CYAN.format(formattedString);
            }
        }
    };
}
