package com.sl5r0.qwobot.domain.help;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;

import java.util.Comparator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.sl5r0.qwobot.core.IrcTextFormatter.*;

public class Command {
    public static final Ordering<Command> triggerOrdering = Ordering.from(new Comparator<Command>() {
        @Override
        public int compare(Command c1, Command c2) {
            return c1.trigger().compareTo(c2.trigger());
        }
    });

    private final String trigger;
    private final String description;
    private final List<String> parameters = newArrayList();
    private int parameterCount = 0;

    public Command(String trigger, String description) {
        this.trigger = checkNotNull(trigger, "trigger must not be null");
        this.description = checkNotNull(description, "description must not be null");
    }

    public int parameterCount() {
        return parameterCount;
    }

    public Command addParameter(String parameter) {
        parameters.add(parameter);
        parameterCount++;
        return this;
    }

    public Command addOptionalParameter(String parameter) {
        checkNotNull(parameter, "parameter must not be null");
        parameters.add(parameter);
        return this;
    }

    public Command addUnboundedParameter(String parameter) {
        checkNotNull(parameter, "parameter must not be null");
        return addParameter(parameter + " 1> ... <" + parameter + " N");
    }

    public String trigger() {
        return trigger;
    }

    public String description() {
        return description;
    }

    public String prettyString() {
        final List<String> prettyParameters = transform(parameters, new Function<String, String>() {
            @Override
            public String apply(String input) {
                return "<" + input + ">";
            }
        });

        final String formattedArguments = GREEN.format(Joiner.on(" ").join(prettyParameters));
        final String formattedTrigger = TEAL.format(trigger);
        return BOLD.format("Usage: ") + formattedTrigger + " " + formattedArguments + BOLD.format(" - " + description);
    }

    @Override
    public String toString() {
        return "Command{" +
                "trigger='" + trigger + '\'' +
                ", description='" + description + '\'' +
                ", parameters=" + parameters +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Command command = (Command) o;

        if (description != null ? !description.equals(command.description) : command.description != null) return false;
        if (parameters != null ? !parameters.equals(command.parameters) : command.parameters != null)
            return false;
        //noinspection RedundantIfStatement
        if (trigger != null ? !trigger.equals(command.trigger) : command.trigger != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = trigger != null ? trigger.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }
}
