package com.sl5r0.qwobot.plugins.commands;

import com.google.common.collect.ImmutableSet;
import org.hamcrest.CustomTypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.sl5r0.qwobot.helpers.UnitTestHelpers.*;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

public class CompoundCommandTest {
    private static final String PARAMETERIZED_COMMAND = "parameterizedCommand";
    private static final String TRIGGER_COMMAND = "triggerCommand";
    private static final String COMPOUND_COMMAND = "compoundCommand";
    private static final String TRIGGER_COMMAND_HELP = "triggerHelp";
    private static final String PARAMETERIZED_COMMAND_HELP = "parameterHelp";
    private static final String TRIGGER = "trigger";

    private CompoundCommand command;
    private ParameterizedTriggerCommand parameterizedCommand = mock(ParameterizedTriggerCommand.class);
    private TriggerCommand triggerCommand = mock(TriggerCommand.class);
    private MessageEvent event = mockMessageEvent(mockUser(), mockChannel());
    private String expectedEventMessage;

    @Before
    public void setUp() throws Exception {
        when(parameterizedCommand.getTrigger()).thenReturn(PARAMETERIZED_COMMAND);
        when(parameterizedCommand.getHelp()).thenReturn(singletonList(PARAMETERIZED_COMMAND_HELP));
        when(triggerCommand.getTrigger()).thenReturn(TRIGGER_COMMAND);
        when(triggerCommand.getHelp()).thenReturn(singletonList(TRIGGER_COMMAND_HELP));
        when(event.getMessage()).thenReturn(TRIGGER + " some stuff");
        command = new CompoundCommand(TRIGGER, ImmutableSet.of(parameterizedCommand, triggerCommand));
        expectedEventMessage = event.getMessage().substring(command.getTrigger().length()).trim();
    }

    @Test
    public void ensureParametersAreCorrectlyPassedWhenSubcommandIsParameterizedCommand() throws Exception {
        command.execute(event, newArrayList(PARAMETERIZED_COMMAND, "param2"));
        verify(parameterizedCommand).execute(argThat(hasMessage(expectedEventMessage)), eq(newArrayList("param2")));
        verify(parameterizedCommand).getTrigger();
        verify(triggerCommand).getTrigger();
        verifyNoMoreInteractions(triggerCommand);
    }

    @Test
    public void ensureEventIsCorrectlyPassedWhenSubcommandIsTriggerCommand() throws Exception {
        command.execute(event, newArrayList(TRIGGER_COMMAND, "param2"));
        verify(triggerCommand).onMessageEvent(argThat(hasMessage(expectedEventMessage)));
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

    @Test
    public void ensureGetCommandsRetrunsAllInnerCommands() throws Exception {
        final Map<String,TriggerCommand> commands = command.getCommands();
        assertThat(commands.entrySet(), hasSize(2));
        assertThat(commands.keySet(), containsInAnyOrder(TRIGGER_COMMAND, PARAMETERIZED_COMMAND));
    }

    @Test
    public void ensureThatCompoundCommandsCanGenerateCorrectHelpStrings() throws Exception {
        final CompoundCommand compoundCommand = new CompoundCommand(COMPOUND_COMMAND, newHashSet(triggerCommand, parameterizedCommand));
        command = new CompoundCommand(TRIGGER, Collections.<TriggerCommand>singleton(compoundCommand));

        final List<String> help = command.getHelp();
        assertThat(help, hasSize(2));
        assertThat(help, containsInAnyOrder(
                TRIGGER + " " + COMPOUND_COMMAND + " " + PARAMETERIZED_COMMAND_HELP,
                TRIGGER + " " + COMPOUND_COMMAND + " " + TRIGGER_COMMAND_HELP
        ));
    }

    private CustomTypeSafeMatcher<MessageEvent> hasMessage(final String expectedEventMessage) {
        return new CustomTypeSafeMatcher<MessageEvent>("a MessageEvent with message") {
            @Override
            protected boolean matchesSafely(MessageEvent messageEvent) {
                return messageEvent.getMessage().equals(expectedEventMessage);
            }
        };
    }
}