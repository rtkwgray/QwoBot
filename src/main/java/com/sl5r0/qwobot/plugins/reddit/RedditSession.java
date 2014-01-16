package com.sl5r0.qwobot.plugins.reddit;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

class RedditSession {
    private static final String BASE_URL = "https://ssl.reddit.com/";
    private static final GenericUrl LOGIN_URL = new GenericUrl(BASE_URL + "api/login");
    private static final GenericUrl SUBMIT_URL = new GenericUrl(BASE_URL + "api/submit");
    private static final Logger log = LoggerFactory.getLogger(RedditSession.class);

    private final HttpRequestFactory requestFactory;
    private final RedditRequestInitializer requestInitializer;

    // TODO: get rid of this flag. It's just to stop Reddit from being spammed if we're not logged in, but it should
    // be handled in a different way (maybe detecting login failure).
    private boolean isLoggedIn = false;

    RedditSession(NetHttpTransport netHttpTransport) {
        requestInitializer = new RedditRequestInitializer();
        requestFactory = netHttpTransport.createRequestFactory(requestInitializer);
    }

    void login(String username, String password) {
        final RedditLoginRequest loginRequest = new RedditLoginRequest(username, password);
        try {
            final HttpRequest request = requestFactory.buildPostRequest(LOGIN_URL, new UrlEncodedContent(loginRequest));
            final RedditResponse response = request.execute().parseAs(RedditResponse.class);
            requestInitializer.setCookie(checkNotNull(response.getCookie(), "Reddit cookie was null"));
            requestInitializer.setModHash(checkNotNull(response.getModHash(), "Reddit modhash was null."));
            isLoggedIn = true;
        } catch (IOException e) {
            log.warn("Could not login to Reddit", e);
            throw new RuntimeException(e);
        }
    }

    void postLink(String subReddit, String title, String url) throws IOException {
        if (!isLoggedIn) {
            log.warn("Can't post " + url + " to " + subReddit + " because we're not logged in.");
            return;
        }

        final RedditSubmitRequest submitRequest = new RedditSubmitRequest(subReddit, title, url);
        final HttpRequest request = requestFactory.buildPostRequest(SUBMIT_URL, new UrlEncodedContent(submitRequest));
        request.execute();
    }
}
