package com.sl5r0.qwobot.domain;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class Channel {
    public final Set<String> halfOps;
    public final Set<String> normalUsers;
    public final Set<String> ops;
    public final Set<String> owners;
    public final Set<String> superOps;
    public final Set<String> users;
    public final Set<String> voices;
    public final String channelKey;
    public final String mode;
    public final String name;
    public final String topic;
    public final String topicSetter;
    public final int channelLimit;
    public final long topicTimestamp;
    public final long createTimestamp;
    public final boolean isInviteOnly;
    public final boolean isModerated;
    public final boolean isNoExternalMessages;
    public final boolean isSecret;

    public Channel(org.pircbotx.Channel channel) {
        halfOps = copyUserSet(channel.getHalfOps());
        normalUsers = copyUserSet(channel.getNormalUsers());
        ops = copyUserSet(channel.getOps());
        owners = copyUserSet(channel.getOwners());
        superOps = copyUserSet(channel.getSuperOps());
        users = copyUserSet(channel.getUsers());
        voices = copyUserSet(channel.getVoices());
        channelKey = channel.getChannelKey();
        mode = channel.getMode();
        name = channel.getName();
        topic = channel.getTopic();
        topicSetter = channel.getTopicSetter();
        channelLimit = channel.getChannelLimit();
        topicTimestamp = channel.getTopicTimestamp();
        createTimestamp = channel.getCreateTimestamp();
        isInviteOnly = channel.isInviteOnly();
        isModerated = channel.isModerated();
        isNoExternalMessages = channel.isNoExternalMessages();
        isSecret = channel.isSecret();
    }

    private static Set<String> copyUserSet(Set<org.pircbotx.User> users) {
        return ImmutableSet.copyOf(
                Collections2.transform(users, new Function<org.pircbotx.User, String>() {
                    @Override
                    public String apply(org.pircbotx.User user) {
                        return user.getNick();
                    }
                })
        );
    }

    @Override
    public String toString() {
        return "Channel{" +
                "halfOps=" + halfOps +
                ", normalUsers=" + normalUsers +
                ", ops=" + ops +
                ", owners=" + owners +
                ", superOps=" + superOps +
                ", users=" + users +
                ", voices=" + voices +
                ", channelKey='" + channelKey + '\'' +
                ", mode='" + mode + '\'' +
                ", name='" + name + '\'' +
                ", topic='" + topic + '\'' +
                ", topicSetter='" + topicSetter + '\'' +
                ", channelLimit=" + channelLimit +
                ", topicTimestamp=" + topicTimestamp +
                ", createTimestamp=" + createTimestamp +
                ", isInviteOnly=" + isInviteOnly +
                ", isModerated=" + isModerated +
                ", isNoExternalMessages=" + isNoExternalMessages +
                ", isSecret=" + isSecret +
                '}';
    }
}
