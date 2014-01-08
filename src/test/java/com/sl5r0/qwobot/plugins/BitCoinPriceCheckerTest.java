package com.sl5r0.qwobot.plugins;

import com.sl5r0.qwobot.api.QwoBot;
import com.sl5r0.qwobot.domain.MessageEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class BitCoinPriceCheckerTest {
    private MessageEvent messageEvent;
    private QwoBot qwoBot;

    @Before
    public void setUp() throws Exception {
        messageEvent = mock(MessageEvent.class);
        when(messageEvent.message()).thenReturn("!btc usd cad");

        qwoBot = mock(QwoBot.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                System.out.println(invocation.getArguments()[0]);
                return null;
            }
        }).when(qwoBot).sendMessageToAllChannels(anyString());
    }

    @Test
    public void ensureFilterWorks() throws Exception {
        BitCoinPriceChecker bitCoinPriceChecker = new BitCoinPriceChecker(qwoBot);
        bitCoinPriceChecker.processMessageEvent(messageEvent);
    }
}
