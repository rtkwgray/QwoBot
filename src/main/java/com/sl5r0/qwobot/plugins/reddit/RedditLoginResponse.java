package com.sl5r0.qwobot.plugins.reddit;

import com.google.api.client.util.Key;

import java.util.List;

public class RedditLoginResponse {
    @Key
    List<List<String>> errors;

    @Key
    RedditLoginData data;

    static class RedditLoginData {
        @Key
        String modhash;
        @Key
        String cookie;
    }
}
