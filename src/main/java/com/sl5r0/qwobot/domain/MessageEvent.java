package com.sl5r0.qwobot.domain;

public class MessageEvent {
    public final User user;
    public final Channel channel;
    public final String message;

    public MessageEvent(org.pircbotx.hooks.events.MessageEvent messageEvent) {
        user = new User(messageEvent.getUser());
        channel = new Channel(messageEvent.getChannel());
        message = messageEvent.getMessage();
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "user=" + user +
                ", channel=" + channel +
                ", message='" + message + '\'' +
                '}';
    }
}
