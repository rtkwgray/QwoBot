package com.sl5r0.qwobot.core;

import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.slf4j.LoggerFactory.getLogger;

public class ShutdownNotifier {
    private static final Logger log = getLogger(ShutdownNotifier.class);
    private final CompletableFuture<String> future = new CompletableFuture<>();

    public void shutdown(String reason) {
        future.complete(reason);
    }

    public void awaitShutdown() {
        while (true) {
            try {
                final String shutdownCause = future.get();
                log.info("Shutting down. Reason: " + shutdownCause);
                System.exit(0);
            } catch (InterruptedException | ExecutionException e) {
                log.warn("Unexpected exception while waiting for shutdown.", e);
            }
        }
    }
}
