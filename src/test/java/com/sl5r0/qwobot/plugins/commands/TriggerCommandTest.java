package com.sl5r0.qwobot.plugins.commands;

import org.junit.Before;
import org.junit.Test;
import org.pircbotx.hooks.events.MessageEvent;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TriggerCommandTest {
    private static final String TRIGGER = "trigger";
    private MessageEvent event;
    private TestTriggerCommand command;

    @Before
    public void setUp() throws Exception {
        event = mock(MessageEvent.class);
        command = new TestTriggerCommand();
    }

    @Test
    public void ensureTriggeredIsCalledWhenMessageStartsWithTrigger() throws Exception {
        when(event.getMessage()).thenReturn("something else");
        command.onMessageEvent(event);

        assertThat(command.wasTriggered, equalTo(false));
    }

    @Test
    public void ensureTriggeredIsNotCalledWhenMessageDoesNotStartWithTrigger() throws Exception {
        when(event.getMessage()).thenReturn(TRIGGER);
        command.onMessageEvent(event);

        assertThat(command.wasTriggered, equalTo(true));
    }

    private static class TestTriggerCommand extends TriggerCommand {
        private boolean wasTriggered = false;

        public TestTriggerCommand() {
            super(TRIGGER);
        }

        @Override
        protected void triggered(MessageEvent event) {
            wasTriggered = true;
        }
    }
}
