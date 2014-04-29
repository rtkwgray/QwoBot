package com.sl5r0.qwobot.helpers;

import org.junit.rules.ExternalResource;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

// This should be a newTestablePircBot that sets up a testable bot. It should also be able to create events as consistently as possible, and these events should contain verifiable
// parameters. For example, a MessageEvent should contain a verifiable user and a verifiable channel.
public class TestableIrcServer extends ExternalResource {
    private Set<String> users = newHashSet();
    private ServerSocket listener;

    @Override
    protected void before() throws Throwable {
        listener = new ServerSocket(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Socket accept = listener.accept();
                    new ClientConnection(accept).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void after() {
        try {
            listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int port() {
        return listener.getLocalPort();
    }


    public void connectPircBotX(PircBotX pircBotX) {
        final String serverHostname = pircBotX.getConfiguration().getServerHostname();
        try {
            pircBotX.getInputParser().handleLine(":" + serverHostname + " 001 QwoBot :Welcome to the Internet Relay Network QwoBot !~QwoBot@184.175.47.68");
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }
    }

//    public void connectUser() {
//        try {
//            bot.getInputParser().handleLine(":seagray!~wgray@localhost JOIN :#qwobot");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (IrcException e) {
//            e.printStackTrace();
//        }
//    }




//    public PrivateMessageEvent<PircBotX> newPrivateMessageEvent(String message, User from) {
//        return new PrivateMessageEvent<PircBotX>(this, from, message);
//    }
//
//    public VerifiableUser newUser(String nick) {
//        return new VerifiableUser(this, nick);
//    }
//
//    public VerifiableChannel newChannel(String name) {
//        return new VerifiableChannel(this, testableUserChannelDao, name);
//    }
//
//
//
//    public static PrivateMessageEvent<PircBotX> mockPrivateMessage(User from, String message) {
//        return new PrivateMessageEvent<>(mockBot(), from, message);
//    }
//
//    public static JoinEvent<PircBotX> joinEvent() {
//        return new JoinEvent<>(mockBot(), mockChannel(), mockUser());
//    }
//
//    public static PircBotX mockBot() {
//        return new PircBotX(new Configuration.Builder<>().setServerHostname("localhost").buildConfiguration());
//    }
//
//    public static User mockUser() {
//        final User user = mock(User.class);
//        when(user.send()).thenReturn(mock(OutputUser.class));
//        return user;
//    }
//
//    public static Channel mockChannel() {
//        final Channel channel = mock(Channel.class);
//        when(channel.send()).thenReturn(mock(OutputChannel.class));
//        return channel;
//    }

    private class ClientConnection {
        private final Socket socket;
        private String nickName;
        private String userName;

        private ClientConnection(Socket socket) {
            this.socket = socket;

        }

        public void start() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final BufferedReader in;
                    final PrintWriter out;
                    try {
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        out = new PrintWriter(socket.getOutputStream(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            socket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        return;
                    }

                    while(true) {
                        String line = null;
                        try {
                            line = in.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (line == null) {
                            break;
                        }

                        List<String> tokens = tokenize(line);
                        if (tokens.get(0).equals("NICK")) {
                            nickName = tokens.get(1);
                        } else if (tokens.get(0).equals("USER")) {
                            userName = tokens.get(1);
                            out.println(":localhost 001 " + nickName + " :Welcome to the Internet Relay Network " + nickName + " !~" + userName + "@" + socket.getInetAddress().getHostName());
                        }
//                        System.out.println(line);
                    }
                }
            }).start();
        }
    }

    private List<String> tokenize(String line) {
        return newArrayList(line.split(" "));
    }
}