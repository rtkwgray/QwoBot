package com.sl5r0.qwobot.plugins.reddit;

import com.sl5r0.qwobot.plugins.commands.RegexCommand;
import org.jsoup.Jsoup;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class PostLinkToReddit extends RegexCommand {
    private static final String URL_REGEX = "(https?://\\S+\\.\\S+)";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
    private static final Logger log = LoggerFactory.getLogger(PostLinkToReddit.class);

    private final RedditSession session;
    private final String subReddit;

    public PostLinkToReddit(RedditSession session, String subReddit) {
        super(URL_PATTERN);
        this.session = session;
        this.subReddit = subReddit;
    }

    @Override
    public void execute(MessageEvent event, List<String> arguments) {
        for (String url : arguments) {
            log.info("Creating Reddit post for: " + url);
            String title;
            try {
                title = getTitle(url);
            } catch (IOException e) {
                log.warn("Unable to fetch title for " + url);
                title = event.getUser().getNick() + "'s link";
            }

            try {
                session.postLink(subReddit, title, url);
            } catch (IOException e) {
                log.warn("Could not post " + url + " to Reddit", e);
            }
        }
    }

    private String getTitle(String url) throws IOException {
        return Jsoup.connect(url).get().title();
    }
}
