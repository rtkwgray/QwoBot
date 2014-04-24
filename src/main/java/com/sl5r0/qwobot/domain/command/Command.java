package com.sl5r0.qwobot.domain.command;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.Subscribe;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.sl5r0.qwobot.core.IrcTextFormatter.*;
import static com.sl5r0.qwobot.domain.command.Parameter.toDescription;
import static org.slf4j.LoggerFactory.getLogger;

public class Command<T extends GenericMessageEvent> {
    public static final Ordering<Command> triggerOrdering = Ordering.from(new Comparator<Command>() {
        @Override
        public int compare(Command c1, Command c2) {
            return c1.trigger().compareTo(c2.trigger());
        }
    });
    protected final Logger log = getLogger(getClass());
    private final Class<T> eventType;
    private final Optional<String> description;
    private final List<Parameter> parameters;
    private final CommandHandler<T> handler;

    public Command(Class<T> eventType, List<Parameter> parameters, Optional<String> description, CommandHandler<T> handler) {
        this.eventType = checkNotNull(eventType, "eventType must not be null");
        this.handler = checkNotNull(handler, "handler must not be null");
        this.description = checkNotNull(description, "description must not be null");
        this.parameters = copyOf(checkNotNull(parameters, "parameters must not be null"));
    }

    @Subscribe
    public final void handle(GenericMessageEvent<PircBotX> event) {
        if (eventType.isAssignableFrom(event.getClass())) {
            try {
                handler.handle(eventType.cast(event), parseArguments(event.getMessage()));
            } catch (NoSuchElementException ignored) {
            } catch (RuntimeException e) {
                log.error("Uncaught exception during handler execution", e);
            }
        }
    }

    private List<String> parseArguments(String message) {
        final List<String> parsedArguments = newArrayList();
        String searchString = message;
        for (Parameter parameter : parameters) {
            searchString = parameter.find(searchString, parsedArguments);
        }
        return parsedArguments;
    }

    public String trigger() {
        return null;
    }

    public String description() {
        return description.or("");
    }

    public String prettyString() {
        final List<String> prettyParameters = transform(parameters, toDescription);

        final String formattedArguments = GREEN.format(Joiner.on(" ").join(prettyParameters));
        final String formattedTrigger = TEAL.format(null);
        return BOLD.format("Usage: ") + formattedTrigger + " " + formattedArguments + BOLD.format(" - " + description);
    }

    public static <E extends GenericMessageEvent> Builder<E> forEvent(Class<E> eventType) {
        return new Builder<>(eventType);
    }

    public static final class Builder<E extends GenericMessageEvent> {
        private final Class<E> eventType;
        private final List<Parameter> parameters = newArrayList();
        private Optional<String> description = absent();
        private CommandHandler<E> handler;

        private Builder(Class<E> eventType) {
            this.eventType = checkNotNull(eventType);
        }

        public Builder<E> addParameter(Parameter parameter) {
            parameters.add(checkNotNull(parameter, "parameter must not be null"));
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
            checkState(!parameters.isEmpty(), "command must have at least one parameter");
            checkState(handler != null, "command must have a handler");
            return new Command<>(eventType, parameters, description, handler);
        }
    }
}
