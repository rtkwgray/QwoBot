package com.sl5r0.qwobot.thirdparty.reddit;

import com.google.api.client.util.Key;

public class RedditSubmitRequest {
    public RedditSubmitRequest(String subreddit, String title, String url) {
        this.subreddit = subreddit;
        this.title = title;
        this.url = url;
    }

    @Key
    String api_type = "json";
    @Key
    String kind = "link";
    @Key
    boolean resubmit = true;
    @Key
    boolean save = false;
    @Key("sr")
    String subreddit;
    @Key
    String title;
    @Key
    String url;

    @Override
    public String toString() {
        return "RedditSubmitRequest{" +
                "api_type='" + api_type + '\'' +
                ", kind='" + kind + '\'' +
                ", resubmit=" + resubmit +
                ", save=" + save +
                ", subreddit='" + subreddit + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
