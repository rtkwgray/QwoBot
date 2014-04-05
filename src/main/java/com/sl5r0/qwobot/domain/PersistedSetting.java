package com.sl5r0.qwobot.domain;

import com.sl5r0.qwobot.persistence.PersistenceConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import static com.google.common.base.Preconditions.checkNotNull;

@Entity
@Table(name = "settings")
public class PersistedSetting {
    @Id
    private String key;

    @Column(nullable = false)
    private String value;

    public PersistedSetting(String key, String value) {
        this.key = checkNotNull(key, "key must not be null");
        this.value = checkNotNull(value, "value must not be null");
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @PersistenceConstructor
    private PersistedSetting() {}
}
