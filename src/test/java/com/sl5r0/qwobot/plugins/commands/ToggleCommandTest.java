package com.sl5r0.qwobot.plugins.commands;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.pircbotx.hooks.events.MessageEvent;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ToggleCommandTest {

    @Test
    public void ensureOnMeansTrue() throws Exception {
        TestToggleCommand command = new TestToggleCommand("prefix");
        command.execute(null, ImmutableList.of("on"));
        assertThat(command.lastValue, equalTo(true));
    }

    @Test
    public void ensureOffMeansFalse() throws Exception {
        TestToggleCommand command = new TestToggleCommand("prefix");
        command.execute(null, ImmutableList.of("off"));
        assertThat(command.lastValue, equalTo(false));
    }

    private static class TestToggleCommand extends ToggleCommand {
        private boolean lastValue;

        public TestToggleCommand(String prefix) {
            super(prefix);
        }

        @Override
        protected void execute(MessageEvent event, boolean value) {
            lastValue = value;
        }
    }
}
