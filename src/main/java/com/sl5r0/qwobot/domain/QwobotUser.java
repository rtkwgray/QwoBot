package com.sl5r0.qwobot.domain;

import com.sl5r0.qwobot.persistence.PersistenceConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

@Entity
@Table(name = "users")
public class QwobotUser {
    @Column(unique = true)
    private String nick;

    @Column(nullable = false)
    private int balance;

    @ManyToMany(fetch = EAGER, cascade = ALL)
    private Set<Role> roles;

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private long id;

    public QwobotUser(String nick) {
        this.nick = checkNotNull(nick, "nick must not be null");
        this.balance = 100;
        this.roles = newHashSet();
    }

    @PersistenceConstructor
    private QwobotUser() {
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

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public void modifyBalance(int change) {
        setBalance(getBalance() + change);
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "QwobotUser{" +
                "nick='" + nick + '\'' +
                ", balance=" + balance +
                ", roles=" + roles +
                ", id=" + id +
                '}';
    }

    public boolean hasRole(Role role) {
        checkNotNull(role, "role must not be null");
        return roles.contains(role);
    }
}
