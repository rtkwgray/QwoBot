package com.sl5r0.qwobot.plugins.commands;

import com.google.common.collect.ImmutableList;
import com.sl5r0.qwobot.plugins.exceptions.CommandExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class ToggleCommandTest {

    private static final String TRIGGER = "trigger";
    private TestToggleCommand command;

    @Before
    public void setUp() throws Exception {
        command = new TestToggleCommand();

    }

    @Test
    public void ensureOnMeansTrue() throws Exception {
        command.execute(null, ImmutableList.of("on"));
        assertThat(command.lastValue, equalTo(true));
    }

    @Test
    public void ensureOffMeansFalse() throws Exception {
        command.execute(null, ImmutableList.of("off"));
        assertThat(command.lastValue, equalTo(false));
    }

    @Test(expected = CommandExecutionException.class)
    public void ensureExceptionIsThrownWhenNoArgumentIsProvided() throws Exception {
        command.execute(null, Collections.<String>emptyList());
    }

    @Test(expected = CommandExecutionException.class)
    public void ensureExceptionIsThrownWhenInvalidArgument() throws Exception {
        command.execute(null, ImmutableList.of("invalid"));
    }

    @Test
    public void ensureGetHelpReturnsUsefulHelpMessage() throws Exception {
        assertThat(command.getHelp(), contains(TRIGGER + " <on|off>"));
    }

    private static class TestToggleCommand extends ToggleCommand {
        private boolean lastValue;

        public TestToggleCommand() {
            super(TRIGGER);
        }

        @Override
        protected void execute(MessageEvent event, boolean value) {
            lastValue = value;
        }
    }
}
