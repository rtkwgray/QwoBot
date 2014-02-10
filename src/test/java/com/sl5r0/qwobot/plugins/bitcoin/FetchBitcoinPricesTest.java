package com.sl5r0.qwobot.plugins.bitcoin;

import org.junit.Before;
import org.junit.Test;
import org.pircbotx.hooks.events.MessageEvent;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.mock;

public class FetchBitcoinPricesTest {
    private MessageEvent event;

    @Before
    public void setUp() throws Exception {
        event = mock(MessageEvent.class);
    }

    @Test
    public void testName() throws Exception {
        final FetchBitcoinPrices command = new FetchBitcoinPrices();
        command.execute(event, newArrayList("cad"));
    }
}
