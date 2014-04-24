package com.sl5r0.qwobot.domain.command;

import com.sl5r0.qwobot.helpers.PircBotTestableObjectFactory;
import org.junit.Test;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static com.sl5r0.qwobot.domain.command.Parameter.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.pircbotx.Colors.removeColors;

public class CommandTest {
    TestableCommandHandler recordingHandler = new TestableCommandHandler();

    @Test
    public void ensureLiteralParametersAreParsedCorrectly() throws Exception {
        final Command<GenericMessageEvent> command = Command.forEvent(GenericMessageEvent.class).addParameters(literal("literal")).handler(recordingHandler).build();
        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("literal");
        command.handle(event);

        assertThat(recordingHandler.events.containsKey(event), is(true));
        assertThat(recordingHandler.events.get(event), contains("literal"));
    }

    @Test
    public void ensureIntegerParametersAreParsedCorrectly() throws Exception {
        final Command<GenericMessageEvent> command = Command.forEvent(GenericMessageEvent.class).addParameters(integer("integer")).handler(recordingHandler).build();
        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("10");
        command.handle(event);

        assertThat(recordingHandler.events.containsKey(event), is(true));
        assertThat(recordingHandler.events.get(event), contains("10"));
    }

    @Test
    public void ensureUrlParametersAreParsedCorrectly() throws Exception {
        final Command<GenericMessageEvent> command = Command.forEvent(GenericMessageEvent.class).addParameters(url()).handler(recordingHandler).build();
        final MessageEvent<PircBotX> httpEvent = new PircBotTestableObjectFactory().messageEvent("http://example.com");
        final MessageEvent<PircBotX> httpsEvent = new PircBotTestableObjectFactory().messageEvent("https://example.com");
        command.handle(httpEvent);
        command.handle(httpsEvent);

        assertThat(recordingHandler.events.containsKey(httpEvent), is(true));
        assertThat(recordingHandler.events.containsKey(httpsEvent), is(true));
        assertThat(recordingHandler.events.get(httpEvent), contains("http://example.com"));
        assertThat(recordingHandler.events.get(httpsEvent), contains("https://example.com"));
    }

    @Test
    public void ensureStringParametersAreParsedCorrectly() throws Exception {
        final Command<GenericMessageEvent> command = Command.forEvent(GenericMessageEvent.class).addParameters(string("string")).handler(recordingHandler).build();
        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("arbitraryString");
        command.handle(event);

        assertThat(recordingHandler.events.containsKey(event), is(true));
        assertThat(recordingHandler.events.get(event), contains("arbitraryString"));
    }

    @Test
    public void ensureCompoundCommandsAreParsedCorrectlyStringParametersAreParsedCorrectly() throws Exception {
        final Command<GenericMessageEvent> command = Command.forEvent(GenericMessageEvent.class).addParameters(literal("!trigger"), string("param1"), integer("param2")).handler(recordingHandler).build();
        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("!trigger arg1 10");
        command.handle(event);

        assertThat(recordingHandler.events.containsKey(event), is(true));
        assertThat(recordingHandler.events.get(event), contains("!trigger", "arg1", "10"));
    }

    @Test
    public void ensureAllRepeatingParametersAreCaptured() throws Exception {
        final Command<GenericMessageEvent> command = Command.forEvent(GenericMessageEvent.class).addParameters(repeating(string("param"))).handler(recordingHandler).build();
        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("string1 string2 string3");
        command.handle(event);

        assertThat(recordingHandler.events.containsKey(event), is(true));
        assertThat(recordingHandler.events.get(event), contains("string1", "string2", "string3"));
    }

    @Test
    public void ensureOptionalParametersAreNotRequired() throws Exception {
        final Command<GenericMessageEvent> command = Command.forEvent(GenericMessageEvent.class).addParameters(optional(string("trigger"))).handler(recordingHandler).build();
        final MessageEvent<PircBotX> emptyEvent = new PircBotTestableObjectFactory().messageEvent("");
        final MessageEvent<PircBotX> nonEmptyEvent = new PircBotTestableObjectFactory().messageEvent("something");
        command.handle(emptyEvent);
        command.handle(nonEmptyEvent);

        assertThat(recordingHandler.events.containsKey(emptyEvent), is(true));
        assertThat(recordingHandler.events.containsKey(nonEmptyEvent), is(true));
        assertThat(recordingHandler.events.get(emptyEvent), hasSize(0));
        assertThat(recordingHandler.events.get(nonEmptyEvent), contains("something"));
    }

    @Test
    public void ensureUrlsCanBeAnywhereInString() {
        final Command<GenericMessageEvent> command = Command.forEvent(GenericMessageEvent.class).addParameters(anywhere(url())).handler(recordingHandler).build();
        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("*** http://google.com *** https://example.com ***");
        command.handle(event);

        assertThat(recordingHandler.events.containsKey(event), is(true));
        assertThat(recordingHandler.events.get(event), contains("http://google.com", "https://example.com"));
    }

    @Test
    public void ensureCommandIsNotExecutedOnInvalidLiteral() {
        final Command<GenericMessageEvent> command = Command.forEvent(GenericMessageEvent.class).addParameters(literal("right")).handler(recordingHandler).build();
        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("wrong");
        command.handle(event);

        assertThat(recordingHandler.events.containsKey(event), is(false));
    }

    @Test
    public void ensureCommandIsNotExecutedOnInvalidInteger() {
        final Command<GenericMessageEvent> command = Command.forEvent(GenericMessageEvent.class).addParameters(integer("integer")).handler(recordingHandler).build();
        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("not an integer");
        command.handle(event);

        assertThat(recordingHandler.events.containsKey(event), is(false));
    }

    @Test
    public void ensureCommandIsNotExecutedOnInvalidUrl() {
        final Command<GenericMessageEvent> command = Command.forEvent(GenericMessageEvent.class).addParameters(url()).handler(recordingHandler).build();
        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("not a url");
        command.handle(event);

        assertThat(recordingHandler.events.containsKey(event), is(false));
    }

    @Test (expected = IllegalStateException.class)
    public void ensureCannotAddMoreParametersAfterAddingOptionalParameter() {
        Command.forEvent(GenericMessageEvent.class).addParameters(optional(url()), url()).handler(recordingHandler).build();
    }

    @Test (expected = IllegalStateException.class)
    public void ensureCannotAddMoreParametersAfterAddingRepeating() {
        Command.forEvent(GenericMessageEvent.class).addParameters(repeating(url()), url()).handler(recordingHandler).build();
    }

    @Test
    public void ensureUsageStringIsCorrect() {
        final Command<GenericMessageEvent> command = Command.forEvent(GenericMessageEvent.class).addParameters(literal("!trigger"), string("param1"), integer("param2"), url()).handler(recordingHandler).description("description").build();

        final String usage = removeColors(command.usageString());
        assertThat(usage, is("!trigger <param1> <param2> <url> - description"));
    }


    private static class TestableCommandHandler implements CommandHandler<GenericMessageEvent> {
        private final Map<GenericMessageEvent, List<String>> events = newHashMap();

        @Override
        public void handle(GenericMessageEvent event, List<String> arguments) {
            events.put(event, arguments);
        }
    }
}
