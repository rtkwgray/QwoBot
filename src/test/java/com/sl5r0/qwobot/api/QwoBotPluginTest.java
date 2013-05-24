package com.sl5r0.qwobot.api;

import com.sl5r0.qwobot.domain.MessageEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@RunWith(MockitoJUnitRunner.class)
public class QwoBotPluginTest {

    @InjectMocks
    private PircBotX bot;

    @Mock
    private User user;

    @Mock
    private Channel channel;

    @Test
    public void ensureQuotedParametersInMiddleOfStringWorks() {
        final String message = "!trigger with \"quoted text\" and stuff";
        MessageEvent messageEvent = new MessageEvent(new org.pircbotx.hooks.events.MessageEvent<>(bot, channel, user, message));
        List<String> parameters = QwoBotPlugin.getParametersFromEvent(messageEvent);
        assertThat(parameters, hasSize(5));
        assertThat(parameters.get(0), equalTo("!trigger"));
        assertThat(parameters.get(1), equalTo("with"));
        assertThat(parameters.get(2), equalTo("quoted text"));
        assertThat(parameters.get(3), equalTo("and"));
        assertThat(parameters.get(4), equalTo("stuff"));
    }

    @Test
    public void ensureQuotedParametersAtBeginningOfStringWorks() {
        final String message = "\"quoted text\" and stuff";
        MessageEvent messageEvent = new MessageEvent(new org.pircbotx.hooks.events.MessageEvent<>(bot, channel, user, message));
        List<String> parameters = QwoBotPlugin.getParametersFromEvent(messageEvent);
        assertThat(parameters, hasSize(3));
        assertThat(parameters.get(0), equalTo("quoted text"));
        assertThat(parameters.get(1), equalTo("and"));
        assertThat(parameters.get(2), equalTo("stuff"));
    }

    @Test
    public void ensureQuotedParametersAtEndOfStringWorks() {
        final String message = "stuff and \"quoted text\"";
        MessageEvent messageEvent = new MessageEvent(new org.pircbotx.hooks.events.MessageEvent<>(bot, channel, user, message));
        List<String> parameters = QwoBotPlugin.getParametersFromEvent(messageEvent);
        assertThat(parameters, hasSize(3));
        assertThat(parameters.get(0), equalTo("stuff"));
        assertThat(parameters.get(1), equalTo("and"));
        assertThat(parameters.get(2), equalTo("quoted text"));
    }

    @Test
    public void ensureMultipleSpacesOnlyCountAsOneDelimiter() {
        final String message = "two   spaces";
        MessageEvent messageEvent = new MessageEvent(new org.pircbotx.hooks.events.MessageEvent<>(bot, channel, user, message));
        List<String> parameters = QwoBotPlugin.getParametersFromEvent(messageEvent);
        assertThat(parameters, hasSize(2));
        assertThat(parameters.get(0), equalTo("two"));
        assertThat(parameters.get(1), equalTo("spaces"));
    }

    @Test
    public void ensureEmptyStringReturnsEmptyList() {
        final String message = "";
        MessageEvent messageEvent = new MessageEvent(new org.pircbotx.hooks.events.MessageEvent<>(bot, channel, user, message));
        List<String> parameters = QwoBotPlugin.getParametersFromEvent(messageEvent);
        assertThat(parameters, hasSize(0));
    }
}