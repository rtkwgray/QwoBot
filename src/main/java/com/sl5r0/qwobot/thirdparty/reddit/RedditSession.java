package com.sl5r0.qwobot.thirdparty.reddit;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;

import static com.google.common.base.Preconditions.checkState;
import static org.slf4j.LoggerFactory.getLogger;

class RedditSession {
    private static final Logger log = getLogger(RedditSession.class);
    private static final String BASE_URL = "https://ssl.reddit.com/";
    private static final GenericUrl LOGIN_URL = new GenericUrl(BASE_URL + "api/login");
    private static final GenericUrl SUBMIT_URL = new GenericUrl(BASE_URL + "api/submit");

    private final RateLimiter rateLimiter = RateLimiter.create(0.5);

    private HttpRequestFactory requestFactory;

    void login(String username, String password) {
        log.info("Attempting to log in to Reddit with username \"" + username + "\"");
        final RedditLoginRequest loginRequest = new RedditLoginRequest(username, password);
        try {
            final RedditRequestInitializer requestInitializer = new RedditRequestInitializer(username);
            final HttpRequestFactory httpRequestFactory = new NetHttpTransport().createRequestFactory(requestInitializer);
            final HttpRequest request = httpRequestFactory.buildPostRequest(LOGIN_URL, new UrlEncodedContent(loginRequest));
            final RedditResponse response = request.execute().parseAs(RedditResponse.class);
            requestInitializer.setHeaders(response.getCookie(), response.getModHash());
            requestFactory = httpRequestFactory;
            log.info("Successfully logged in to Reddit with username \"" + username + "\"");
        } catch (IOException | NullPointerException e) {
            log.error("Reddit accountLogin failed. Please check your credentials.", e);
        }
    }

    boolean isAuthenticated() {
        return requestFactory != null;
    }

    void postLink(String subReddit, String title, URI uri) throws IOException {
        checkState(isAuthenticated(), "must be logged in to post links to reddit.");

        final RedditSubmitRequest submitRequest = new RedditSubmitRequest(subReddit, title, uri.toASCIIString());
        log.trace(submitRequest.toString());

        final HttpRequest request = requestFactory.buildPostRequest(SUBMIT_URL, new UrlEncodedContent(submitRequest));
        log.debug("Submitting Reddit post for " + uri.toASCIIString() + " to executor.");
        rateLimiter.acquire();
        final HttpResponse httpResponse = request.execute();
        if (httpResponse.getStatusCode() != 200) {
            log.error("Couldn't post to reddit for some reason. Response was: " + httpResponse.parseAsString());
        } else {
            log.debug("Posted reddit link: " + uri.toASCIIString());
        }
    }
}
