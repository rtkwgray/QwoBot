package com.sl5r0.qwobot.plugins.commands;

import com.google.common.base.Function;
import com.sl5r0.qwobot.plugins.exceptions.CommandExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;

import static com.sl5r0.qwobot.plugins.commands.ParameterizedTriggerCommand.TO_LOWERCASE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

public class ParameterTriggerCommandTest {

    private static final String TRIGGER = "trigger";
    public static final String EXCEPTION_MESSAGE = "exception message";
    private MessageEvent event;
    private Channel channel;
    private TestParameterizedTriggerCommand command;

    @Before
    public void setUp() throws Exception {
        channel = mock(Channel.class);
        event = mock(MessageEvent.class);
        when(event.getChannel()).thenReturn(channel);
        command = new TestParameterizedTriggerCommand();
    }

    @Test
    public void ensureThatNoArgumentsProducesEmptyParameterList() throws Exception {
        when(event.getMessage()).thenReturn(TRIGGER);
        command.triggered(event);

        assertThat(command.lastArguments, hasSize(0));
    }

    @Test
    public void ensureThatWhiteSpaceIsIgnoredWhenMessageIsJustTrigger() throws Exception {
        when(event.getMessage()).thenReturn(TRIGGER + " ");
        command.triggered(event);

        assertThat(command.lastArguments, hasSize(0));
    }


    @Test
    public void ensureThatOriginalMessageEventIsPassedThroughToExecute() throws Exception {
        command.triggered(event);

        assertThat(command.lastEvent, equalTo(event));
    }

    @Test
    public void ensureThatMutatorIsAppliedToAllArguments() throws Exception {
        when(event.getMessage()).thenReturn(TRIGGER + " SOMETHING UPPERCASE");
        command = new TestParameterizedTriggerCommand(TO_LOWERCASE);
        command.triggered(event);

        assertThat(command.lastArguments, hasSize(2));
        assertThat(command.lastArguments.get(0), equalTo("something"));
        assertThat(command.lastArguments.get(1), equalTo("uppercase"));
    }

    @Test
    public void ensureThatQuotedArgumentsAreParsedCorrectly() throws Exception {
        when(event.getMessage()).thenReturn(TRIGGER + " \"something in quotes\" more \"ending quotation");
        command.triggered(event);

        assertThat(command.lastArguments, hasSize(4));
        assertThat(command.lastArguments.get(0), equalTo("something in quotes"));
        assertThat(command.lastArguments.get(1), equalTo("more"));
        assertThat(command.lastArguments.get(2), equalTo("\"ending"));
        assertThat(command.lastArguments.get(3), equalTo("quotation"));
    }

    @Test
    public void ensureThatEndQuotedArgumentsAreParsedCorrectly() throws Exception {
        when(event.getMessage()).thenReturn(TRIGGER + " another quotation\"");
        command.triggered(event);

        assertThat(command.lastArguments, hasSize(2));
        assertThat(command.lastArguments.get(0), equalTo("another"));
        assertThat(command.lastArguments.get(1), equalTo("quotation\""));
    }

    @Test
    public void ensureThatSingleQuoteArgumentsAreParsedCorrectly() throws Exception {
        when(event.getMessage()).thenReturn(TRIGGER + " \"");
        command.triggered(event);

        assertThat(command.lastArguments, hasSize(1));
        assertThat(command.lastArguments.get(0), equalTo("\""));
    }

    @Test
    public void ensureExceptionsAreHandledCorreclty() throws Exception {
        ExceptionThrowingParameterizedTriggerCommand command = new ExceptionThrowingParameterizedTriggerCommand();
        command.triggered(event);

        verify(channel).sendMessage(EXCEPTION_MESSAGE);
        verify(channel).sendMessage("Usage: " + command.getHelp());
        verifyNoMoreInteractions(channel);
    }

    @Test
    public void ensureThatTriggerCanExistInArguments() throws Exception {
        when(event.getMessage()).thenReturn(TRIGGER + " " + TRIGGER);
        command.triggered(event);

        assertThat(command.lastArguments, hasSize(1));
        assertThat(command.lastArguments.get(0), equalTo(TRIGGER));
    }

    private static class TestParameterizedTriggerCommand extends ParameterizedTriggerCommand {
        private MessageEvent lastEvent;
        private List<String> lastArguments;

        public TestParameterizedTriggerCommand() {
            super(TRIGGER);
        }

        public TestParameterizedTriggerCommand(Function<String, String> mutator) {
            super(TRIGGER, mutator);
        }

        @Override
        public void execute(MessageEvent event, List<String> parameters) {
            lastArguments = parameters;
            lastEvent = event;
        }
    }

    private static class ExceptionThrowingParameterizedTriggerCommand extends ParameterizedTriggerCommand {
        public ExceptionThrowingParameterizedTriggerCommand() {
            super(TRIGGER);
        }

        @Override
        public void execute(MessageEvent event, List<String> parameters) {
            throw new CommandExecutionException(EXCEPTION_MESSAGE);
        }
    }
}
