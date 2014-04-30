package com.sl5r0.qwobot.domain;

import com.google.common.base.Function;
import com.sl5r0.qwobot.irc.IrcTextFormatter;
import com.sl5r0.qwobot.persistence.PersistenceConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Entity
@Table(name = "twitter_follows")
public class TwitterFollow {
    public static Function<TwitterFollow, String> toPrettyString = new Function<TwitterFollow, String>() {
        @Override
        public String apply(TwitterFollow input) {
            return input.getStatusColor().format("@" + input.getHandle());
        }
    };

    public static Function<TwitterFollow, Long> toTwitterId = new Function<TwitterFollow, Long>() {
        @Override
        public Long apply(TwitterFollow input) {
            return input.getTwitterId();
        }
    };

    public static Function<TwitterFollow, String> toHandle = new Function<TwitterFollow, String>() {
        @Override
        public String apply(TwitterFollow input) {
            return input.getHandle();
        }
    };

    @Id
    private long twitterId;
    @Column
    private String handle;
    @Column
    private IrcTextFormatter statusColor;

    @PersistenceConstructor
    private TwitterFollow() {
    }

    public TwitterFollow(long twitterId, String handle, IrcTextFormatter statusColor) {
        checkArgument(twitterId >= 0, "twitterId must be >= 0");
        this.twitterId = twitterId;
        this.handle = checkNotNull(handle, "handle must not be null");
        this.statusColor = checkNotNull(statusColor, "statusColor must not be null");
    }

    public long getTwitterId() {
        return twitterId;
    }

    public String getHandle() {
        return handle;
    }

    public IrcTextFormatter getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(IrcTextFormatter statusColor) {
        this.statusColor = checkNotNull(statusColor, "statusColor must not be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TwitterFollow that = (TwitterFollow) o;

        if (twitterId != that.twitterId) return false;
        if (statusColor != that.statusColor) return false;
        //noinspection RedundantIfStatement
        if (handle != null ? !handle.equals(that.handle) : that.handle != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (twitterId ^ (twitterId >>> 32));
        result = 31 * result + (handle != null ? handle.hashCode() : 0);
        result = 31 * result + (statusColor != null ? statusColor.hashCode() : 0);
        return result;
    }
}