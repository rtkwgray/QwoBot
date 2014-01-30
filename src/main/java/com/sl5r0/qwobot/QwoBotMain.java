package com.sl5r0.qwobot;

import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.google.inject.spi.Message;
import com.sl5r0.qwobot.core.QwoBot;
import com.sl5r0.qwobot.core.QwoBotModule;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.collect.Iterables.getFirst;

public class QwoBotMain {
    private static final Logger log = LoggerFactory.getLogger(QwoBotMain.class);
    public static void main(String[] args) throws IOException, IrcException {
        Injector guice = Guice.createInjector(new QwoBotModule());

        try {
            guice.getInstance(QwoBot.class).start();
        } catch (ProvisionException e) {
            log.debug("Stack trace: ", e);
            printHelpfulLogMessageAndExit(e);
        }
    }

    private static void printHelpfulLogMessageAndExit(ProvisionException exception) {
        final Optional<Message> cause = fromNullable(getFirst(exception.getErrorMessages(), null));
        if (cause.isPresent()) {
            // We don't want to rethrow this exception.
            //noinspection ThrowableResultOfMethodCallIgnored
            final Throwable failureReason = cause.get().getCause();
            log.error(failureReason.getMessage());
        } else {
            log.error("An unknown error has occurred during startup. Check debug.log for more information.");
        }
        System.exit(1);
    }
}
