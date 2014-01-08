package com.sl5r0.qwobot.plugins;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.common.eventbus.Subscribe;
import com.sl5r0.qwobot.api.QwoBot;
import com.sl5r0.qwobot.api.QwoBotPlugin;
import com.sl5r0.qwobot.domain.MessageEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Reddit extends QwoBotPlugin {
    public static final String REDDIT_BASE_URL = "https://ssl.reddit.com/";
    private final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(new RedditRequestInitializer());
    private String cookie, modHash, subreddit;

    public Reddit(QwoBot qwoBot, String subreddit) {
        super(qwoBot);
        this.subreddit = subreddit;
    }

    @Subscribe
    public void processMessageEvent(MessageEvent event) {
        String message = event.message();
        if (message.startsWith("http")) {
            try {
                new URL(message);
            } catch (MalformedURLException e) {
                // Not a valid URL, skip this one.
                return;
            }

            String title;
            try {
                Document document = Jsoup.connect(message).get();
                title = document.title();
            } catch (IOException e) {
                // It didn't work, so reset the title.
                title = event.user().nick + "'s link";
            }

            try {

                postLink(title, message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void postLink(String title, String submitUrl) throws IOException {
        GenericUrl url = new GenericUrl(REDDIT_BASE_URL + "api/submit");
        HttpRequest httpRequest = requestFactory.buildPostRequest(url, new UrlEncodedContent(new RedditSubmitRequest(subreddit, title, submitUrl)));
        httpRequest.execute();
    }

    public void loginToReddit(String username, String password) throws IOException {
        GenericUrl url = new GenericUrl(REDDIT_BASE_URL + "api/login");
        HttpRequest httpRequest = requestFactory.buildPostRequest(url, new UrlEncodedContent(new RedditLoginRequest(username, password)));
        RedditResponse redditResponse;
        try {
            redditResponse = httpRequest.execute().parseAs(RedditResponse.class);
        } catch (IllegalArgumentException e) {
            // TODO: clean this up
            throw new IOException(e);
        }
        cookie = redditResponse.json.data.cookie;
        modHash = redditResponse.json.data.modhash;
    }

    @Override
    public String getDescription() {
        return "Reddit integration for posting links to a subreddit";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getHelp() {
        return "Reddit integration for posting links to a subreddit";
    }

    private class RedditRequestInitializer implements HttpRequestInitializer {
        @Override
        public void initialize(HttpRequest request) {
            if (cookie != null) {
                request.getHeaders().setCookie("reddit_session="+cookie);
            }
            if (modHash != null) {
                request.getHeaders().set("X-Modhash", modHash);
            }
            request.getHeaders().setUserAgent("Qwobot Reddit Bot");
            request.setParser(new JsonObjectParser(new JacksonFactory()));
        }
    }

    public static class RedditLoginRequest {
        public RedditLoginRequest(String user, String passwd) {
            this.user = user;
            this.passwd = passwd;
        }

        @Key("api_type") String apiType = "json";
        @Key boolean rem = true;
        @Key String user;
        @Key String passwd;
    }

    public static class RedditLoginResponse {
        @Key List<String> errors;
        @Key RedditLoginData data;
    }

    public static class RedditResponse {
        @Key RedditLoginResponse json;
    }

    public static class RedditLoginData {
        @Key String modhash;
        @Key String cookie;
    }

    public static class RedditSubmitRequest {
        public RedditSubmitRequest(String sr, String title, String url) {
            this.sr = sr;
            this.title = title;
            this.url = url;
        }

        @Key String api_type = "json";
        @Key String kind = "link";
        @Key boolean resubmit = true;
        @Key boolean save = false;
        @Key String sr;
        @Key String title;
        @Key String url;
    }
}