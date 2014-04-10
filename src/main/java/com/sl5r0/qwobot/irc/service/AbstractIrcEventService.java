package com.sl5r0.qwobot.irc.service;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractService;
import org.slf4j.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class AbstractIrcEventService extends AbstractService {
    private final EventBus eventBus;
    protected final Logger log = getLogger(getClass());

    protected AbstractIrcEventService(EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus, "eventBus cannot be null");
    }

    @Override
    protected void doStart() {
        log.info("Starting service.");
        eventBus.register(this);
        notifyStarted();
    }

    @Override
    protected void doStop() {
        log.info("Stopping service.");
        eventBus.unregister(this);
        notifyStarted();
    }

    public static List<String> argumentsFor(String trigger, String message) {
        final Pattern PARAMETER_PATTERN = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        final Matcher matcher = PARAMETER_PATTERN.matcher(message);
        final List<String> parameters = newArrayList();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parameters.add(matcher.group(1));
            } else {
                parameters.add(matcher.group(2));
            }
        }

        if (!parameters.isEmpty() && parameters.get(0).equals(trigger)) {
            return parameters;
        } else {
            return emptyList();
        }
    }
}
