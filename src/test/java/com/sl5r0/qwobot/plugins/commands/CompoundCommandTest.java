package com.sl5r0.qwobot.plugins.commands;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Collections;

import static com.google.common.collect.Lists.newArrayList;
import static com.sl5r0.qwobot.helpers.UnitTestHelpers.*;
import static org.mockito.Mockito.*;

public class CompoundCommandTest {
    private static final String PARAMETERIZED_COMMAND = "parameterizedCommand";
    private static final String TRIGGER_COMMAND = "triggerCommand";
    private static final String TRIGGER = "trigger";

    private CompoundCommand command;
    private ParameterizedTriggerCommand parameterizedCommand = mock(ParameterizedTriggerCommand.class);
    private TriggerCommand triggerCommand = mock(TriggerCommand.class);
    private MessageEvent event = mockMessageEvent(mockUser(), mockChannel());

    @Before
    public void setUp() throws Exception {
        when(parameterizedCommand.getTrigger()).thenReturn(PARAMETERIZED_COMMAND);
        when(triggerCommand.getTrigger()).thenReturn(TRIGGER_COMMAND);
        command = new CompoundCommand(TRIGGER, ImmutableSet.of(parameterizedCommand, triggerCommand));
    }

    @Test
    public void ensureParametersAreCorrectlyPassedWhenSubcommandIsParameterizedCommand() throws Exception {
        command.execute(event, newArrayList(PARAMETERIZED_COMMAND, "param2"));
        verify(parameterizedCommand).execute(event, newArrayList("param2"));
        verify(parameterizedCommand).getTrigger();
        verify(triggerCommand).getTrigger();
        verifyNoMoreInteractions(triggerCommand);
    }

    @Test
    public void ensureEventIsCorrectlyPassedWhenSubcommandIsTriggerCommand() throws Exception {
        command.execute(event, newArrayList(TRIGGER_COMMAND, "param2"));
        verify(triggerCommand).onMessageEvent(event);
        verify(triggerCommand).getTrigger();
        verify(parameterizedCommand).getTrigger();
        verifyNoMoreInteractions(parameterizedCommand);
    }

    @Test (expected = NullPointerException.class)
    public void ensureCommandMapCannotBeNull() throws Exception {
        new CompoundCommand(TRIGGER, null);
    }

    @Test (expected = NullPointerException.class)
    public void ensureTriggerCannotBeNull() throws Exception {
        new CompoundCommand(null, Collections.<TriggerCommand>emptySet());
    }
}
