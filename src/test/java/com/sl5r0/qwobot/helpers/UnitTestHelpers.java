package com.sl5r0.qwobot.helpers;

import com.google.common.eventbus.EventBus;
import com.sl5r0.qwobot.core.BotConfiguration;
import com.sl5r0.qwobot.core.QwoBotListener;
import org.apache.commons.configuration.ConfigurationException;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.output.OutputUser;

import java.io.File;
import java.net.URISyntaxException;

import static com.google.common.io.Resources.getResource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnitTestHelpers {
    public static MessageEvent mockMessageEvent(User user, Channel channel) {
        final MessageEvent event = mock(MessageEvent.class);
        when(event.getUser()).thenReturn(user);
        when(event.getChannel()).thenReturn(channel);
        return event;
    }

    public static User mockUser() {
        final User user = mock(User.class);
        when(user.send()).thenReturn(mock(OutputUser.class));
        return user;
    }

    public static Channel mockChannel() {
        final Channel channel = mock(Channel.class);
        when(channel.send()).thenReturn(mock(OutputChannel.class));
        return channel;
    }

    public static BotConfiguration configFromResource(String resourceName) throws URISyntaxException, ConfigurationException {
        final File configurationFile = new File(getResource(resourceName).toURI());
        return new BotConfiguration(configurationFile, new QwoBotListener(new EventBus()));
    }
}
