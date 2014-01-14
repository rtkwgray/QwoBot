package com.sl5r0.qwobot.plugins.reddit;

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
    @Key("subreddit")
    String subreddit;
    @Key
    String title;
    @Key
    String url;
}
