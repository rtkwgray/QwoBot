package com.sl5r0.qwobot;

import com.sl5r0.qwobot.core.QwoBotInternal;
import org.pircbotx.exception.IrcException;

import java.io.IOException;

public class QwoBotMain {
    public static void main(String[] args) throws IOException, IrcException {
        QwoBotInternal qwoBot = new QwoBotInternal();
        qwoBot.setVerbose(true);
        qwoBot.setName("Qwobot");
        qwoBot.connect("linode.ev98.ca");
        qwoBot.joinChannel("#qwobot");
        qwoBot.setAutoReconnect(true);
    }
}
