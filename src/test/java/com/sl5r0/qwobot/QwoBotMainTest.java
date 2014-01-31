package com.sl5r0.qwobot;

import com.google.inject.Injector;
import com.sl5r0.qwobot.core.QwoBot;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class QwoBotMainTest {
    private QwoBot qwoBot;
    private Injector injector;

    @Before
    public void setUp() throws Exception {
        injector = mock(Injector.class);
        qwoBot = mock(QwoBot.class);
    }

    @Test
    public void ensureStartIsCalledDuringInitialization() throws Exception {
        when(injector.getInstance(QwoBot.class)).thenReturn(qwoBot);
        QwoBotMain qwoBotMain = new QwoBotMain(injector);
        qwoBotMain.initialize();

        verify(qwoBot).start();
    }
}
