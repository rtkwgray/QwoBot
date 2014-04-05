package com.sl5r0.qwobot.helpers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserChannelDao;
import org.pircbotx.output.OutputUser;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class VerifiableUser extends User {
    private final CollectingOutputUser sender;

    public VerifiableUser(PircBotX bot, String nick) {
        super(bot, new UserChannelDao<>(bot, new Configuration.BotFactory()), nick);
        this.sender = new CollectingOutputUser(bot, this);
    }

    @Override
    public OutputUser send() {
        return sender;
    }

    @Override
    public void setNick(String nick) {
        super.setNick(nick);
    }

    public List<String> receivedMessages() {
        return sender.receivedMessages;
    }

    private class CollectingOutputUser extends OutputUser {
        private final List<String> receivedMessages = newArrayList();

        public CollectingOutputUser(PircBotX bot, User user) {
            super(bot, user);
        }

        @Override
        public void message(String message) {
            receivedMessages.add(message);
        }
    }

    public static Matcher<VerifiableUser> hasReceivedMessageContaining(final String expectedMessage) {
        return new TypeSafeMatcher<VerifiableUser>() {
            @Override
            protected boolean matchesSafely(VerifiableUser verifiableUser) {
                for (String message : verifiableUser.receivedMessages()) {
                    if (message.contains(expectedMessage)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a user that received the message \"" + expectedMessage + "\"");
            }
        };
    }
}
