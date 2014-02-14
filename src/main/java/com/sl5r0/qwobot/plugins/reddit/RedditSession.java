package com.sl5r0.qwobot.plugins.reddit;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.util.concurrent.RateLimiter;
import com.sl5r0.qwobot.plugins.exceptions.LoginFailedException;
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
    private final RateLimiter rateLimiter;
    private final String username;
    private final String password;

    private boolean loggedIn = false;
    private boolean loginAlreadyFailed = false;

    RedditSession(NetHttpTransport netHttpTransport, RedditRequestInitializer redditRequestInitializer,
                  RateLimiter rateLimiter, String username, String password) {

        this.requestInitializer = redditRequestInitializer;
        this.username = username;
        this.password = password;
        this.requestFactory = netHttpTransport.createRequestFactory(requestInitializer);
        this.rateLimiter = rateLimiter;
    }

    private void login() throws LoginFailedException {
        if (loginAlreadyFailed) {
            log.warn("Reddit login already failed. Will not retry.");
            throw new LoginFailedException();
        }

        log.info("Logging into Reddit as " + username);
        final RedditLoginRequest loginRequest = new RedditLoginRequest(username, password);
        try {
            final HttpRequest request = requestFactory.buildPostRequest(LOGIN_URL, new UrlEncodedContent(loginRequest));
            final RedditResponse response = request.execute().parseAs(RedditResponse.class);
            requestInitializer.setCookie(checkNotNull(response.getCookie(), "Reddit cookie was null"));
            requestInitializer.setModHash(checkNotNull(response.getModHash(), "Reddit modhash was null."));
            log.info("Successfully logged in to Reddit as " + username);
            loggedIn = true;
        } catch (IOException e) {
            log.warn("Could not login to Reddit (credentials are probably wrong).", e);
            loginAlreadyFailed = true;
            throw new RuntimeException(e);
        }
    }

    void postLink(String subReddit, String title, String url) throws IOException {
        if (!loggedIn) {
            try {
                login();
            } catch (LoginFailedException e) {
                throw new IOException("not logged in to Reddit");
            }
        }

        final RedditSubmitRequest submitRequest = new RedditSubmitRequest(subReddit, title, url);
        log.debug("Created " + submitRequest);

        final HttpRequest request = requestFactory.buildPostRequest(SUBMIT_URL, new UrlEncodedContent(submitRequest));

        log.debug("Submitting reddit post for " + url + " to executor.");
        rateLimiter.acquire();
        request.execute();
        log.debug("Posted reddit link: " + submitRequest.url);
    }
}
