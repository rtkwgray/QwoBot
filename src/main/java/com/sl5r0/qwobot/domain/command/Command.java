package com.sl5r0.qwobot.domain.command;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.Subscribe;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.sl5r0.qwobot.core.IrcTextFormatter.WHITE;
import static com.sl5r0.qwobot.domain.command.Parameter.toFormattedString;
import static com.sl5r0.qwobot.domain.command.Parameter.urlPattern;
import static org.slf4j.LoggerFactory.getLogger;

public class Command<T extends GenericMessageEvent> {
    public static final Ordering<Command> usageStringOrdering = new Ordering<Command>() {
        @Override
        public int compare(Command left, Command right) {
            return left.usageString().compareTo(right.usageString());
        }
    };
    private static final Logger log = getLogger(Command.class);
    private final List<Parameter> parameters;
    private final CommandHandler<T> handler;
    private final Class<T> eventType;
    private final Optional<String> description;

    private Command(List<Parameter> parameters, CommandHandler<T> handler, Class<T> eventType, Optional<String> description) {
        this.parameters = parameters;
        this.handler = handler;
        this.eventType = eventType;
        this.description = description;
    }

    public List<String> parseArguments(String string) throws CommandParseException {
        final Scanner scanner = new Scanner(string);
        final List<String> arguments = newArrayList();
        for (Parameter parameter : parameters) {
            switch (parameter.getType()) {
                case STRING:
                    parseNextString(scanner, arguments, parameter);
                    break;
                case LITERAL:
                    parseNextLiteral(scanner, arguments, parameter);
                    break;
                case INTEGER:
                    parseNextInteger(scanner, arguments, parameter);
                    break;
                case URL:
                    if (parameter.isPositional()) {
                        parseNextUrl(scanner, arguments, parameter);
                    } else {
                        parseAllUrls(scanner, arguments);
                    }
            }
        }

        return arguments;
    }

    @Subscribe
    public final void handle(GenericMessageEvent<PircBotX> event) {
        if (eventType.isAssignableFrom(event.getClass())) {
            try {
                handler.handle(eventType.cast(event), parseArguments(event.getMessage()));
            } catch (RuntimeException e) {
                log.error("Uncaught exception during handler execution", e);
            } catch (CommandParseException ignored) {
            }
        }
    }

    private void parseAllUrls(Scanner scanner, List<String> arguments) {
        while (true) {
            final String inLine = scanner.findInLine(urlPattern);
            if (inLine == null) {
                break;
            }

            arguments.add(inLine);
        }
    }

    private void parseNextUrl(Scanner scanner, List<String> arguments, Parameter parameter) throws CommandParseException {
        try {
            arguments.add(scanner.next(urlPattern));
        } catch (NoSuchElementException ignored) {
            if (parameter.isOptional()) {
                return;
            }
            throw new CommandParseException();
        }

        if (parameter.repeats()) {
            while (true) {
                try {
                    arguments.add(scanner.next(urlPattern));
                } catch (NoSuchElementException ignored) {
                    break;
                }
            }
        }
    }

    private void parseNextString(Scanner scanner, List<String> arguments, Parameter parameter) throws CommandParseException {
        try {
            arguments.add(scanner.next());
        } catch (NoSuchElementException ignored) {
            if (parameter.isOptional()) {
                return;
            }
            throw new CommandParseException();
        }

        if (parameter.repeats()) {
            while (true) {
                try {
                    arguments.add(scanner.next());
                } catch (NoSuchElementException ignored) {
                    break;
                }
            }
        }
    }

    private void parseNextLiteral(Scanner scanner, List<String> arguments, Parameter parameter) throws CommandParseException {
        try {
            arguments.add(scanner.next(parameter.getDescription()));
        } catch (NoSuchElementException ignored) {
            if (parameter.isOptional()) {
                return;
            }
            throw new CommandParseException();
        }

        if (parameter.repeats()) {
            while (true) {
                try {
                    arguments.add(scanner.next(parameter.getDescription()));
                } catch (NoSuchElementException ignored) {
                    break;
                }
            }
        }
    }

    private void parseNextInteger(Scanner scanner, List<String> arguments, Parameter parameter) throws CommandParseException {
        try {
            arguments.add(Integer.toString(scanner.nextInt()));
        } catch (NoSuchElementException ignored) {
            if (parameter.isOptional()) {
                return;
            }
            throw new CommandParseException();
        }

        if (parameter.repeats()) {
            while (true) {
                try {
                    arguments.add(Integer.toString(scanner.nextInt()));
                } catch (NoSuchElementException ignored) {
                    break;
                }
            }
        }
    }

    public String usageString() {
        return Joiner.on(" ").join(transform(parameters, toFormattedString)) + " - " + WHITE.format(description.or(""));
    }

    public static <E extends GenericMessageEvent> Builder<E> forEvent(Class<E> eventType) {
        return new Builder<>(eventType);
    }

    public static final class Builder<E extends GenericMessageEvent> {
        private final Class<E> eventType;
        private final List<Parameter> parameters = newArrayList();
        private Optional<String> description = absent();
        private CommandHandler<E> handler;
        private boolean canAddMoreParameters = true;

        private Builder(Class<E> eventType) {
            this.eventType = checkNotNull(eventType);
        }

        private Builder<E> addParameter(Parameter parameter) {
            checkState(canAddMoreParameters, "cannot add parameter after existing optional or repeating parameter");
            parameters.add(checkNotNull(parameter, "parameter must not be null"));
            canAddMoreParameters = !(parameter.repeats() || parameter.isOptional());
            return this;
        }

        public Builder<E> addParameters(Parameter... parameters) {
            for (Parameter parameter : parameters) {
                addParameter(parameter);
            }
            return this;
        }

        public Builder<E> description(String description) {
            this.description = fromNullable(description);
            return this;
        }

        public Builder<E> handler(CommandHandler<E> handler) {
            this.handler = checkNotNull(handler, "handler must not be null");
            return this;
        }

        public Command<E> build() {
            checkState(handler != null, "command must have a handler");
            return new Command<>(parameters, handler, eventType, description);
        }
    }
}
