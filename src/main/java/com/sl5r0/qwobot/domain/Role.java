package com.sl5r0.qwobot.domain;

import com.sl5r0.qwobot.persistence.PersistenceConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import static com.google.common.base.Preconditions.checkNotNull;

@Entity
@Table(name = "roles")
public class Role {
    public static final String SECURED_ACCOUNT = "securedAccount";

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private long id;

    @Column
    private String name;

    private Role(String roleName) {
        this.name = checkNotNull(roleName, "roleName must not be null");
    }

    @PersistenceConstructor
    private Role() {
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Role securedAccount() {
        return new Role(SECURED_ACCOUNT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        //noinspection RedundantIfStatement
        if (name != null ? !name.equals(role.name) : role.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
