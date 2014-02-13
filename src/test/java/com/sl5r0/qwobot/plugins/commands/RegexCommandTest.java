package com.sl5r0.qwobot.plugins.commands;

import org.junit.Before;
import org.junit.Test;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.regex.Pattern.compile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegexCommandTest {
    private static final String REGEX = "regex";
    private MessageEvent event;
    private TestRegexCommand command;

    @Before
    public void setUp() throws Exception {
        event = mock(MessageEvent.class);
        command = new TestRegexCommand(REGEX);
    }

    @Test
    public void ensureExecuteIsCalledWhenRegexMatches() throws Exception {
        when(event.getMessage()).thenReturn(REGEX);
        command.onMessageEvent(event);

        assertThat(command.wasCalled(), equalTo(true));
        assertThat(command.lastArguments, hasSize(1));
        assertThat(command.lastArguments.get(0), equalTo(REGEX));
    }

    @Test
    public void ensureExecuteIsNotCalledWhenRegexDoesNotMatch() throws Exception {
        when(event.getMessage()).thenReturn("non-matching");
        command.onMessageEvent(event);

        assertThat(command.wasCalled(), equalTo(false));
    }

    @Test
    public void ensureExecuteIsCalledWithAllMatchingStrings() throws Exception {
        when(event.getMessage()).thenReturn(REGEX + "non-matching" + REGEX + "non-matching");
        command.onMessageEvent(event);

        assertThat(command.lastArguments, hasSize(2));
        assertThat(command.lastArguments.get(0), equalTo(REGEX));
        assertThat(command.lastArguments.get(1), equalTo(REGEX));
    }

    @Test
    public void ensureGetHelpReturnsRegex() throws Exception {
        assertThat(command.getHelp(), contains("regex(" + REGEX + ")"));
    }

    private static class TestRegexCommand extends RegexCommand {
        private List<String> lastArguments;

        public TestRegexCommand(String regex) {
            super(compile(regex));
        }

        @Override
        public void execute(MessageEvent event, List<String> parameters) {
            lastArguments = checkNotNull(parameters);
        }

        public boolean wasCalled() {
            return lastArguments != null;
        }
    }
}
