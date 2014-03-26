package com.sl5r0.qwobot.plugins.qbux;

import com.google.common.eventbus.EventBus;
import com.sl5r0.qwobot.plugins.PluginManager;
import org.junit.Test;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import static com.sl5r0.qwobot.core.TestModule.testInjector;
import static com.sl5r0.qwobot.helpers.UnitTestHelpers.*;
import static org.mockito.Mockito.when;

public class QBuxIntegrationTest {
    @Test
    public void testName() throws Exception {
        final EventBus eventBus = new EventBus();
        testInjector().withConfiguration("qwobot-qbux-integration.xml").withEventBus(eventBus).instanceOf(PluginManager.class);

        User user = mockUser();
        when(user.getNick()).thenReturn("warren");
        MessageEvent event = mockMessageEvent(user, mockChannel());
        when(event.getMessage()).thenReturn("!qbux register");

        eventBus.post(event);
    }
}
