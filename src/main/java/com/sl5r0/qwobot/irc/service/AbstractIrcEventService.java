package com.sl5r0.qwobot.irc.service;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractService;
import com.google.inject.Inject;
import com.sl5r0.qwobot.domain.command.Command;
import com.sl5r0.qwobot.domain.command.CommandDirectory;
import org.slf4j.Logger;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class AbstractIrcEventService extends AbstractService {
    protected final Logger log = getLogger(getClass());

    @Inject
    private EventBus eventBus;

    @Inject
    private CommandDirectory commandDirectory;

    private Set<Command> commands = newHashSet();

    protected final void registerCommand(Command<?> command) {
        commands.add(checkNotNull(command, "command must not be null"));
    }

    public AbstractIrcEventService() {
        initialize();
    }

    protected abstract void initialize();

    @Override
    protected void doStart() {
        log.info("Starting service");
        commandDirectory.register(commands);
        eventBus.register(this);
        for (Command command : commands) {
            eventBus.register(command);
        }
        notifyStarted();
    }

    @Override
    protected void doStop() {
        log.info("Stopping service");
        commandDirectory.unregister(commands);
        eventBus.unregister(this);
        for (Command command : commands) {
            eventBus.unregister(command);
        }
        notifyStarted();
    }

//    /**
//     * Parses arguments for if a command begins with a certain trigger
//     * @param command the command that will be executed
//     * @param message the input to parse
//     * @return a parsed list of arguments with size at least argumentCount
//     * @throws CommandNotApplicableException if the command doesn't meet the minimum argument count
//     */
//    public List<String> argumentsFor(Command command, String message) {
//        final Pattern PARAMETER_PATTERN = Pattern.compile("\"([^\"]*)\"|(\\S+)");
//        final Matcher matcher = PARAMETER_PATTERN.matcher(message);
//        final List<String> parameters = newArrayList();
//        while (matcher.find()) {
//            if (matcher.group(1) != null) {
//                parameters.add(matcher.group(1));
//            } else {
//                parameters.add(matcher.group(2));
//            }
//        }
//
//        if (parameters.size() > command.parameterCount() && parameters.get(0).equals(command.trigger())) {
//            parameters.remove(0);
//            log.debug("Executing command " + command + " with parameters " + parameters);
//            return parameters;
//        }
//
//        throw new CommandNotApplicableException(message, command);
//    }
}
