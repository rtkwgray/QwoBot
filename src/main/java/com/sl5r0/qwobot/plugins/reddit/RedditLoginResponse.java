package com.sl5r0.qwobot.plugins.reddit;

import com.google.api.client.util.Key;

public class RedditLoginResponse {
    @Key
    RedditLoginData data;

    public static class RedditLoginData {
        @Key
        String modhash;
        @Key
        String cookie;
    }
}
