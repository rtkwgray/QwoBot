package com.sl5r0.qwobot.domain;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class User {
    public final Set<String> channels;
    public final Set<String> channelsHalfOpIn;
    public final Set<String> channelsOwnerIn;
    public final Set<String> channelsSuperOpIn;
    public final Set<String> channelsVoiceOpIn;
    public final int hops;
    public final String hostmask;
    public final String login;
    public final String nick;
    public final String realName;
    public final String server;
    public final boolean isAway;
    public final boolean isIrcop;
    public final boolean isVerified;

    public User(org.pircbotx.User user) {
        channels = copyChannelSet(user.getChannels());
        channelsHalfOpIn = copyChannelSet(user.getChannelsHalfOpIn());
        channelsOwnerIn = copyChannelSet(user.getChannelsOwnerIn());
        channelsSuperOpIn = copyChannelSet(user.getChannelsSuperOpIn());
        channelsVoiceOpIn = copyChannelSet(user.getChannelsVoiceIn());
        hops = user.getHops();
        hostmask = user.getHostmask();
        login = user.getLogin();
        nick = user.getNick();
        realName = user.getRealName();
        server = user.getServer();
        isAway = user.isAway();
        isIrcop = user.isIrcop();
        isVerified = user.isVerified();
    }

    private static Set<String> copyChannelSet(Set<org.pircbotx.Channel> channels) {
        return ImmutableSet.copyOf(
                Collections2.transform(channels, new Function<org.pircbotx.Channel, String>() {
                    @Override
                    public String apply(org.pircbotx.Channel channel) {
                        return channel.getName();
                    }
                })
        );
    }

    @Override
    public String toString() {
        return "User{" +
                "channels=" + channels +
                ", channelsHalfOpIn=" + channelsHalfOpIn +
                ", channelsOwnerIn=" + channelsOwnerIn +
                ", channelsSuperOpIn=" + channelsSuperOpIn +
                ", channelsVoiceOpIn=" + channelsVoiceOpIn +
                ", hops=" + hops +
                ", hostmask='" + hostmask + '\'' +
                ", login='" + login + '\'' +
                ", nick='" + nick + '\'' +
                ", realName='" + realName + '\'' +
                ", server='" + server + '\'' +
                ", isAway=" + isAway +
                ", isIrcop=" + isIrcop +
                ", isVerified=" + isVerified +
                '}';
    }
}
