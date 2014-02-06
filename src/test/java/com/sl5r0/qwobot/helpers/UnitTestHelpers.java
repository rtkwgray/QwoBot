package com.sl5r0.qwobot.helpers;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.output.OutputUser;

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
}
