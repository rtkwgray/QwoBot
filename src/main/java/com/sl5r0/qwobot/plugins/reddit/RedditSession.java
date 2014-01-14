package com.sl5r0.qwobot.plugins.reddit;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

class RedditSession {
    private static final String BASE_URL = "https://ssl.reddit.com/";
    private static final GenericUrl LOGIN_URL = new GenericUrl(BASE_URL + "api/login");
    private static final GenericUrl SUBMIT_URL = new GenericUrl(BASE_URL + "api/submit");
    private static final Logger log = LoggerFactory.getLogger(RedditSession.class);

    private final HttpRequestFactory requestFactory;
    private final RedditRequestInitializer requestInitializer;

    RedditSession(NetHttpTransport netHttpTransport) {
        requestInitializer = new RedditRequestInitializer();
        requestFactory = netHttpTransport.createRequestFactory(requestInitializer);
    }

    void login(String username, String password) {
        final RedditLoginRequest loginRequest = new RedditLoginRequest(username, password);
        try {
            final HttpRequest request = requestFactory.buildPostRequest(LOGIN_URL, new UrlEncodedContent(loginRequest));
            final RedditResponse response = request.execute().parseAs(RedditResponse.class);
            requestInitializer.setCookie(response.getCookie());
            requestInitializer.setModHash(response.getModHash());
        } catch (IOException e) {
            log.warn("Could not login to Reddit", e);
        }
    }

    void postLink(String subReddit, String title, String url) throws IOException {
        final RedditSubmitRequest submitRequest = new RedditSubmitRequest(subReddit, title, url);
        final HttpRequest request = requestFactory.buildPostRequest(SUBMIT_URL, new UrlEncodedContent(submitRequest));
        request.execute();
    }
}