package com.sl5r0.qwobot.plugins;

import com.sl5r0.qwobot.api.QwoBot;
import com.sl5r0.qwobot.domain.MessageEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedditTest {
    @InjectMocks
    Reddit reddit;

    @Mock
    QwoBot bot;

    @Mock
    MessageEvent messageEvent;

    @Before
    public void setUp() throws Exception {
        when(messageEvent.message()).thenReturn("http://tinyurl.com/2tx");
    }

    @Test
    public void canPluginFetchUrlTitles() throws Exception {
        reddit.processMessageEvent(messageEvent);

    }
}
