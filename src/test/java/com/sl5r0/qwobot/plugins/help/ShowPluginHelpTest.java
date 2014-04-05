package com.sl5r0.qwobot.plugins.help;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provider;
import com.sl5r0.qwobot.plugins.PluginManager;
import com.sl5r0.qwobot.plugins.TestCommand;
import com.sl5r0.qwobot.plugins.commands.Command;
import com.sl5r0.qwobot.plugins.exceptions.CommandExecutionException;
import com.sl5r0.qwobot.plugins.exceptions.PluginNotRegisteredException;
import org.junit.Before;
import org.junit.Test;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class ShowPluginHelpTest {
    private static final String PLUGIN_NAME = "plugin";
    private static final String HELP_STRING = "some help";
    private static final String UNREGISTERED_PLUGIN_NAME = "invalid plugin";
    private ShowPluginHelp command;
    private MessageEvent event;

    @Before
    public void setUp() throws Exception {
        final PluginManager pluginManager = mock(PluginManager.class);
        Provider<PluginManager> pluginManagerProvider = new Provider<PluginManager>() {
            @Override
            public PluginManager get() {
                return pluginManager;
            }
        };
        final Command pluginCommand = new TestCommand(HELP_STRING);
        when(pluginManager.getCommandsForPlugin(PLUGIN_NAME)).thenReturn(ImmutableSet.of(pluginCommand));
        when(pluginManager.getCommandsForPlugin(UNREGISTERED_PLUGIN_NAME)).thenThrow(new PluginNotRegisteredException());
        command = new ShowPluginHelp(pluginManagerProvider);
        event = mock(MessageEvent.class);
    }

    @Test
    public void ensureHelpForCommandsIsSentToUserWhenPluginIsRegistered() throws Exception {
        command.execute(event, ImmutableList.of(PLUGIN_NAME));
        verify(event.getUser().send()).message("Commands for plugin: " + PLUGIN_NAME);
        verify(event.getUser().send()).message(HELP_STRING);
    }

    @Test (expected = CommandExecutionException.class)
    public void ensureExceptionIsThrownWhenPluginIsNotRegistered() throws Exception {
        command.execute(event, ImmutableList.of(UNREGISTERED_PLUGIN_NAME));
    }

    @Test (expected = CommandExecutionException.class)
    public void ensureExceptionIsThrownWhenNoPluginIsSpecified() throws Exception {
        command.execute(event, Collections.<String>emptyList());
    }
}
