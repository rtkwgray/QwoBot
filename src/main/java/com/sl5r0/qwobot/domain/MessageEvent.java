package com.sl5r0.qwobot.domain;

public class MessageEvent {
    private final User user;
    private final Channel channel;
    private final String message;

    public MessageEvent(org.pircbotx.hooks.events.MessageEvent messageEvent) {
        user = new User(messageEvent.getUser());
        channel = new Channel(messageEvent.getChannel());
        message = messageEvent.getMessage();
    }

    public User user() {
        return user;
    }

    public String message() {
        return message;
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
