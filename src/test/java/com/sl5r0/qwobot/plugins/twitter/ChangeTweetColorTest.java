package com.sl5r0.qwobot.plugins.twitter;

import com.google.common.collect.ImmutableList;
import com.sl5r0.qwobot.plugins.exceptions.CommandExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.pircbotx.hooks.events.MessageEvent;
import twitter4j.Twitter;
import twitter4j.TwitterStream;

import static com.sl5r0.qwobot.core.Format.BLUE;
import static com.sl5r0.qwobot.core.Format.GREEN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class ChangeTweetColorTest {
    private ChangeTweetColor command;
    private TwitterState twitterState;
    private MessageEvent event;

    @Before
    public void setUp() throws Exception {
        twitterState = new TwitterState(mock(Twitter.class), mock(TwitterStream.class));
        command = new ChangeTweetColor(twitterState);
        event = mock(MessageEvent.class);
    }

    @Test
    public void ensureColorIsChangedWhenValid() throws Exception {
        assertThat(twitterState.getTweetColor(), equalTo(BLUE));
        command.execute(event, ImmutableList.of("green"));
        assertThat(twitterState.getTweetColor(), equalTo(GREEN));
    }

    @Test (expected = CommandExecutionException.class)
    public void ensureColorIsNotChangedWhenNotValid() throws Exception {
        command.execute(event, ImmutableList.of("not-a-color"));
    }
}
