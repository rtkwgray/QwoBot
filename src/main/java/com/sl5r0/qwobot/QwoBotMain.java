package com.sl5r0.qwobot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sl5r0.qwobot.core.QwoBot;
import com.sl5r0.qwobot.core.QwoBotModule;
import org.pircbotx.exception.IrcException;

import java.io.IOException;

public class QwoBotMain {
    public static void main(String[] args) throws IOException, IrcException {
        Injector guice = Guice.createInjector(new QwoBotModule());
        guice.getInstance(QwoBot.class).start();
    }
}
