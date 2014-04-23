package com.sl5r0.qwobot.domain.command;

import com.google.common.base.Function;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

public class Parameter {
    protected final String description;
    private final Pattern pattern;
    private final boolean repeating;
    private final boolean optional;

    private Parameter(String description, String regex, boolean repeating, boolean optional) {
        this.description = description;
        this.repeating = repeating;
        this.optional = optional;
        this.pattern = compile(regex);
    }

    public boolean isRepeating() {
        return repeating;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public static Parameter exactMatch(String string) {
        return new Parameter(string, "(" + quote(string) + ")", false, false);
    }

    public static Parameter string(String description) {
        return new Parameter("<" + description + ">", "(\\S+)", false, false);
    }

    public static Parameter number(String description) {
        return new Parameter("<" + description + ">", "(\\d+)", false, false);
    }

    public static Parameter url() {
        return new Parameter("<url>", "(https?://\\S+\\.\\S+)", false, false);
    }

    public static Parameter repeating(Parameter parameter) {
        return new Parameter(parameter.description, parameter.pattern.pattern() + "+", true, parameter.optional);
    }

    public static Parameter optional(Parameter parameter) {
        return new Parameter(parameter.description, "(" + parameter.pattern.pattern() + "+)?", parameter.repeating, true);
    }

    public String find(String string, List<String> matches) {
        final Matcher matcher = pattern.matcher(string);
        if (repeating) {
            int endIndex = 0;
            while(matcher.find()) {
                matches.add(matcher.group(1));
                endIndex = matcher.end();
            }
            return string.substring(endIndex);
        } else {
            if (matcher.find()) {
                matches.add(matcher.group(1));
                return string.substring(matcher.end());
            }
        }

        if (optional) {
            return string;
        } else {
            throw new NoSuchElementException();
        }
    }

    public static final Function<Parameter, String> toRegex = new Function<Parameter, String>() {
        @Override
        public String apply(Parameter input) {
            return input.pattern.pattern();
        }
    };

    public static final Function<Parameter, String> toDescription = new Function<Parameter, String>() {
        @Override
        public String apply(Parameter input) {
            return input.description;
        }
    };
}
