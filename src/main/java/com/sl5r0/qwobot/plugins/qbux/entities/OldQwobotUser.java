package com.sl5r0.qwobot.plugins.qbux.entities;

import com.google.common.base.Optional;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name = "accounts")
public class OldQwobotUser {
    private long id;
    private String nick;
    private long balance = 0;

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public long getId() {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setId(long id) {
        this.id = id;
    }

    @Column(unique = true)
    public String getNick() {
        return nick;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setNick(String nick) {
        this.nick = nick;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        checkArgument(balance >= 0, "cannot set balance to negative value");
        this.balance = balance;
    }

    public void modifyBalance(long amount) {
        checkArgument(balance + amount >= 0, "cannot set balance to negative value");
        this.balance += amount;
    }

    @Override
    public String toString() {
        return "OldQwobotUser{" +
                "id=" + id +
                ", nick='" + nick + '\'' +
                ", balance=" + balance +
                '}';
    }

    public static Optional<OldQwobotUser> findByNick(String nick, Session session) {
        final Object user = session.createQuery("from OldQwobotUser user where user.nick = :nick").setString("nick", nick).uniqueResult();
        return fromNullable(OldQwobotUser.class.cast(user));
    }
}
