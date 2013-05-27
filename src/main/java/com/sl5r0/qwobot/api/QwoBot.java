package com.sl5r0.qwobot.api;

import com.sl5r0.qwobot.domain.Channel;
import com.sl5r0.qwobot.domain.User;

import java.util.Set;

public interface QwoBot {
    public void registerPlugin(QwoBotPlugin qwoBotPlugin);

    public User getUserDetails(String nick);

    public Channel getChannelDetails(String name);

    public Set<Plugin> getLoadedPlugins();

    public void sendMessageToUser(User user, String message);

    public void sendMessageToChannel(Channel channel, String message);

    public void sendMessageToAllChannels(String message);
}
