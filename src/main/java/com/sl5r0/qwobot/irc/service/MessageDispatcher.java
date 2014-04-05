package com.sl5r0.qwobot.irc.service;

import com.google.common.eventbus.Subscribe;
import com.sl5r0.qwobot.irc.service.runnables.MessageRunnable;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public class MessageDispatcher {
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\"([^\"]*)\"|(\\S+)");
    private final Map<Predicate<GenericMessageEvent<PircBotX>>, MessageRunnable> messageHandlers = newHashMap();
    private final Map<Predicate<GenericMessageEvent<PircBotX>>, MessageRunnable> privateMessageHandlers = newHashMap();

    @Subscribe
    public void dispatch(MessageEvent<PircBotX> event) {
        doDispatch(messageHandlers, event);
    }

    @Subscribe
    public void dispatch(PrivateMessageEvent<PircBotX> event) {
        doDispatch(privateMessageHandlers, event);
    }

    MessageDispatcher subscribeToMessage(Predicate<GenericMessageEvent<PircBotX>> predicate, MessageRunnable runnable) {
        messageHandlers.put(predicate, runnable);
        return this;
    }

    MessageDispatcher subscribeToPrivateMessage(Predicate<GenericMessageEvent<PircBotX>> predicate, MessageRunnable runnable) {
        privateMessageHandlers.put(predicate, runnable);
        return this;
    }

    public static Predicate<GenericMessageEvent<PircBotX>> startingWith(final String string) {
        return new Predicate<GenericMessageEvent<PircBotX>>() {
            @Override
            public boolean test(GenericMessageEvent<PircBotX> event) {
                return event.getMessage().startsWith(string);
            }
        };
    }

    private void doDispatch(Map<Predicate<GenericMessageEvent<PircBotX>>, MessageRunnable> handlerMap, GenericMessageEvent<PircBotX> event) {
        final Set<Map.Entry<Predicate<GenericMessageEvent<PircBotX>>, MessageRunnable>> handlers = handlerMap.entrySet();
        for (Map.Entry<Predicate<GenericMessageEvent<PircBotX>>, MessageRunnable> handler : handlers) {
            if (handler.getKey().test(event)) {
                handler.getValue().run(event, parseArguments(event.getMessage()));
            }
        }
    }

    private List<String> parseArguments(String message) {
        final Matcher matcher = PARAMETER_PATTERN.matcher(message);
        final List<String> parameters = newArrayList();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parameters.add(matcher.group(1));
            } else {
                parameters.add(matcher.group(2));
            }
        }
        return parameters;
    }

}
