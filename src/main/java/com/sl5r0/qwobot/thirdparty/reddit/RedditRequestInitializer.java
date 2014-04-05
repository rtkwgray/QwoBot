package com.sl5r0.qwobot.thirdparty.reddit;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import static com.google.common.base.Preconditions.checkNotNull;

class RedditRequestInitializer implements HttpRequestInitializer {
    private final String userAgent;
    private String cookie;
    private String modHash;

    RedditRequestInitializer(String redditUser) {
        this.userAgent = "QwoBot Reddit/IRC Bot (v.1.0) for user: " + checkNotNull(redditUser, "redditUser must not be null");
    }

    void setHeaders(String cookie, String modHash) {
        this.cookie = checkNotNull(cookie, "cookie must not be null");
        this.modHash = checkNotNull(modHash, "modHash must not be null");
    }

    boolean hasHeaders() {
        return cookie != null && modHash != null;
    }

    @Override
    public void initialize(HttpRequest request) {
        if (cookie != null) {
            request.getHeaders().setCookie("reddit_session=" + cookie);
        }
        if (modHash != null) {
            request.getHeaders().set("X-Modhash", modHash);
        }

        request.getHeaders().setUserAgent(userAgent);
        request.setParser(new JsonObjectParser(new JacksonFactory()));
    }
}
