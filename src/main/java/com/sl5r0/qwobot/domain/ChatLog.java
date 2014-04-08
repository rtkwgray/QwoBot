package com.sl5r0.qwobot.domain;

import com.sl5r0.qwobot.persistence.PersistenceConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import javax.persistence.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.joda.time.DateTime.now;

@Entity
@Table(name = "chat_logs")
public class ChatLog {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private long id;

    @Column
    private String nick;

    @Column
    private String channel;

    @Column
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime created;

    @Column(length = 512)
    private String message;

    public ChatLog(String nick, String channel, String message) {
        this.nick = checkNotNull(nick, "nick must not be null");
        this.channel = checkNotNull(channel, "channel must not be null");
        this.message = checkNotNull(message, "message must not be null");
        this.created = now();
    }

    public static ChatLog fromMessageEvent(MessageEvent<PircBotX> event) {
        return new ChatLog(event.getUser().getNick(), event.getChannel().getName(), event.getMessage());
    }

    public String getNick() {
        return nick;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    @PersistenceConstructor
    private ChatLog() {}
}
