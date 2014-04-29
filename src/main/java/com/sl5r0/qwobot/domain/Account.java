package com.sl5r0.qwobot.domain;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.sl5r0.qwobot.persistence.PersistenceConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Sets.newHashSet;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

@Entity
@Table(name = "account")
public class Account {
    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private int balance;

    @ManyToMany(fetch = EAGER, cascade = ALL)
    private Set<Role> roles;

    @Column
    private String password;

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private long id;

    public Account(String username) {
        this.username = checkNotNull(username, "username must not be null");
        this.roles = newHashSet();
    }

    @PersistenceConstructor
    private Account() {
    }

    public String getUsername() {
        return username;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public Optional<String> getPassword() {
        return fromNullable(password);
    }

    public Set<String> getRoles() {
        return ImmutableSet.copyOf(transform(roles, new Function<Role, String>() {
            @Override
            public String apply(Role input) {
                return input.getName();
            }
        }));
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + balance;
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", balance=" + balance +
                ", roles=" + roles +
                ", id=" + id +
                '}';
    }
}
