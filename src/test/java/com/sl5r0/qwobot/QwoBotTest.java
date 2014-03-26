package com.sl5r0.qwobot;

import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class QwoBotTest {
    private com.sl5r0.qwobot.core.QwoBot qwoBot;
    private Injector injector;

    @Before
    public void setUp() throws Exception {
        injector = mock(Injector.class);
        qwoBot = mock(com.sl5r0.qwobot.core.QwoBot.class);
    }

    @Test
    public void ensureStartIsCalledDuringInitialization() throws Exception {
        when(injector.getInstance(com.sl5r0.qwobot.core.QwoBot.class)).thenReturn(qwoBot);
//        QwoBot qwoBotMain = new QwoBot(injector);
//        qwoBotMain.initialize();

        verify(qwoBot).start();
    }
}
