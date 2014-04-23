package com.sl5r0.qwobot.domain.help;

import com.sl5r0.qwobot.domain.command.Command;
import com.sl5r0.qwobot.domain.command.CommandHandler;
import com.sl5r0.qwobot.domain.command.Parameter;
import com.sl5r0.qwobot.helpers.PircBotTestableObjectFactory;
import org.junit.Test;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Optional.of;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.sl5r0.qwobot.domain.command.Parameter.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class CommandTest {
    @Test
    public void ensureArgumentsAreParsedCorrectly() throws Exception {
        final TestableCommand<MessageEvent> command = new TestableCommand<>(MessageEvent.class, newArrayList(exactMatch("!trigger"), string("a name"), number("a number")), "something");
        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("!trigger something 90");
        command.handle(event);

        final List<String> parsedArguments = command.executions.get(event);
        assertThat(parsedArguments, hasSize(3));
        assertThat(parsedArguments, contains("!trigger", "something", "90"));
    }

    @Test
    public void ensureCommandIsNotExecutedWhenMessageDoesNotMatch() throws Exception {
        final TestableCommand<MessageEvent> command = new TestableCommand<>(MessageEvent.class, newArrayList(exactMatch("!trigger"), string("a name"), number("a number")), "something");
        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("!nomatch");
        command.handle(event);

        assertThat(command.executions.values(), hasSize(0));
    }

    @Test
    public void ensureOptionalCommandsWorkAtEndOfString() throws Exception {
        final TestableCommand<MessageEvent> command = new TestableCommand<>(MessageEvent.class, newArrayList(exactMatch("!trigger"), optional(number("a number"))), "something");
        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("!trigger");
        command.handle(event);

        assertThat(command.executions.values(), hasSize(1));
    }


    @Test
    public void ensureRepeatingParametersAreCorrectlyParsed() throws Exception {
        final TestableCommand<MessageEvent> command = new TestableCommand<>(MessageEvent.class, newArrayList(repeating((url()))), "something");
        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("http://google.com not a url https://example.com more junk http://funny.com");
        command.handle(event);

        assertThat(command.executions.values(), hasSize(1));
        final List<String> parsedArguments = command.executions.get(event);
        assertThat(parsedArguments, hasSize(3));
        assertThat(parsedArguments, contains("http://google.com", "https://example.com", "http://funny.com"));
    }

    @Test
    public void xxh() throws Exception {
        final TestableCommand<MessageEvent> command = new TestableCommand<>(MessageEvent.class, newArrayList(exactMatch("!trigger"), repeating(string("a name"))), "something");
        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("!trigger this is a string");
        command.handle(event);

        assertThat(command.executions.values(), hasSize(1));
        final List<String> parsedArguments = command.executions.get(event);
        assertThat(parsedArguments, contains("!trigger", "this", "is", "a", "string"));
    }


    private static class TestableCommand<T extends GenericMessageEvent> extends Command<T> {
        private Map<T, List<String>> executions = newHashMap();

        public TestableCommand(Class<T> eventType, List<Parameter> parameters, String description) {
            super(eventType, parameters, of(description), new CommandHandler<T>() {
                @Override
                public void handle(T event, List<String> arguments) {
                    System.out.println("something");
                }
            });
        }

    }

    // TODO:
    // should commands be associated with a particular event class?
    // perhaps a command can be generic and accept a class as a parameter with an abstract "execute" method
    // the execute method should call another method from the service class.
    // the abstract service should have a single subscribe entry point that listens to all events.
    // the service should look at the commands, find ones that take the specific event type, and then execute those events


}
