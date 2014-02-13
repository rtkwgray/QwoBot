package com.sl5r0.qwobot.plugins.bitcoin;

import org.junit.Test;
import org.pircbotx.hooks.events.MessageEvent;

import static com.google.common.collect.Lists.newArrayList;
import static com.sl5r0.qwobot.helpers.UnitTestHelpers.*;

public class FetchBitcoinPricesTest {
    private MessageEvent event = mockMessageEvent(mockUser(), mockChannel());

    @Test // TODO: implement test
    public void testName() throws Exception {
        final FetchBitcoinPrices command = new FetchBitcoinPrices();
        command.execute(event, newArrayList("cad"));
    }
}
