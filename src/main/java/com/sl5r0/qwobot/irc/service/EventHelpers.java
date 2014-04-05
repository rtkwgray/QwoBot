package com.sl5r0.qwobot.irc.service;

import com.google.common.hash.HashCode;
import org.pircbotx.User;

import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.hash.Hashing.sha512;

public class EventHelpers {
    static List<String> getArguments(String message) {
        return copyOf(message.split(" "));
    }

    static HashCode getUserHash(User user) {
        final String userMask = user.getNick() + "|" + user.getLogin() + "@" + user.getHostmask() + "(" + user.getRealName() + ")";
        return sha512().hashString(userMask, UTF_8);
    }
}
