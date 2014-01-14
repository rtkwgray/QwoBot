package com.sl5r0.qwobot.plugins.reddit;

import com.google.api.client.util.Key;

public class RedditLoginRequest {
    public RedditLoginRequest(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Key("api_type")
    String apiType = "json";
    @Key
    boolean rem = true;
    @Key
    String user;
    @Key("password")
    String password;
}
