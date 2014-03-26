package com.sl5r0.qwobot.domain;

import com.sl5r0.qwobot.persistence.PersistenceConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

@Entity
public class QwobotUser {
    private UUID authenticationToken;
    private String hostMask;

    QwobotUser(UUID authenticationToken, String hostMask) {
        this.authenticationToken = checkNotNull(authenticationToken, "authenticationToken must not be null");
        this.hostMask = checkNotNull(hostMask, "hostMask must not be null");
    }

    @PersistenceConstructor
    private QwobotUser() {
    }

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private long id;

    public long getId() {
        return id;
    }

    @Column(unique = true)
    public UUID getAuthenticationToken() {
        return authenticationToken;
    }

    @Column(unique = true)
    public String getHostMask() {
        return hostMask;
    }

    @Override
    public String toString() {
        return "QwobotUser{" +
                "id=" + id +
                ", authenticationToken=" + authenticationToken +
                ", hostMask='" + hostMask + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QwobotUser that = (QwobotUser) o;

        if (id != that.id) return false;
        if (authenticationToken != null ? !authenticationToken.equals(that.authenticationToken) : that.authenticationToken != null)
            return false;
        //noinspection RedundantIfStatement
        if (hostMask != null ? !hostMask.equals(that.hostMask) : that.hostMask != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = authenticationToken != null ? authenticationToken.hashCode() : 0;
        result = 31 * result + (hostMask != null ? hostMask.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }
}
