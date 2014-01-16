package com.sl5r0.qwobot.plugins.reddit;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import static com.google.common.base.Preconditions.checkNotNull;

class RedditRequestInitializer implements HttpRequestInitializer {
    private String cookie;
    private String modHash;

    RedditRequestInitializer(String redditUser) {
        this.redditUser = checkNotNull(redditUser);
    }

    private final String redditUser;

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public void setModHash(String modHash) {
        this.modHash = modHash;
    }

    @Override
    public void initialize(HttpRequest request) {
        if (cookie != null) {
            request.getHeaders().setCookie("reddit_session=" + cookie);
        }
        if (modHash != null) {
            request.getHeaders().set("X-Modhash", modHash);
        }
        request.getHeaders().setUserAgent("QwoBot Reddit/IRC Bot (v.1.0) for user: " + redditUser);
        request.setParser(new JsonObjectParser(new JacksonFactory()));
    }
}
