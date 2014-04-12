package com.sl5r0.qwobot.irc.service;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractService;
import com.sl5r0.qwobot.irc.service.exceptions.CommandNotApplicableException;
import org.slf4j.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
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

    /**
     * Parses arguments for if a command begins with a certain trigger
     * @param trigger the trigger to match
     * @param message the input to parse
     * @param argumentCount the minimum number of arguments for the command to be considered valid (not including the trigger)
     * @return a parsed list of arguments with size at least argumentCount
     * @throws CommandNotApplicableException if the command doesn't meet the minimum argument count
     */
    public static List<String> argumentsFor(String trigger, String message, int argumentCount) {
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

        if (parameters.size() >= argumentCount + 1 && parameters.get(0).equals(trigger)) {
            parameters.remove(0);
            return parameters;
        }

        throw new CommandNotApplicableException(message);
    }
}
