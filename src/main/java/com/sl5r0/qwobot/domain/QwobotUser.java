package com.sl5r0.qwobot.domain;

import com.sl5r0.qwobot.persistence.PersistenceConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Entity
@Table(name = "users")
public class QwobotUser {
    @Column(unique = true)
    private String nick;

    @Column(nullable = false)
    private int balance;

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private long id;

    public QwobotUser(String nick) {
        this.nick = checkNotNull(nick, "nick must not be null");
        this.balance = 100;
    }

    public String getNick() {
        return nick;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        checkArgument(balance >= 0, "balance must be positive");
        this.balance = balance;
    }

    public void modifyBalance(int change) {
        setBalance(getBalance() + change);
    }

    @PersistenceConstructor
    private QwobotUser() { }
}
