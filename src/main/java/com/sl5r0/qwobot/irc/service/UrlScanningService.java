package com.sl5r0.qwobot.irc.service;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.help.Command;
import com.sl5r0.qwobot.thirdparty.reddit.Reddit;
import com.sl5r0.qwobot.thirdparty.reddit.RedditPostFailedException;
import org.jsoup.Jsoup;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sl5r0.qwobot.core.IrcTextFormatter.BOLD;

@Singleton
public class UrlScanningService extends AbstractIrcEventService {
    private static final Pattern URL_PATTERN = Pattern.compile("(https?://\\S+\\.\\S+)");
    private final Reddit reddit;

    @Inject
    public UrlScanningService(Reddit reddit) {
        super(Collections.<Command>emptySet());
        this.reddit = checkNotNull(reddit, "reddit must not be null");
    }

    @Subscribe
    public void scanUrls(MessageEvent<PircBotX> event) {
        for (String url : findUrls(event.getMessage())) {
            String pageTitle = url;
            try {
                pageTitle = Jsoup.connect(url).get().title();
            } catch (IOException e) {
                log.debug("Couldn't fetch a page title for " + url, e);
            }

            final String channelMessage = pageTitle.length() > 400 ? pageTitle.substring(0, 400) + "..." : pageTitle;
            // TODO: keep a cache of recent links (i.e. within the last 20 minutes or so) and don't repost them.
            // TODO: clean this up too
            if (!pageTitle.equals(url)) {
                event.getChannel().send().message(BOLD.format(channelMessage));
            }

            try {
                reddit.post(pageTitle, url);
            } catch (RedditPostFailedException e) {
                log.info("Not posting " + url + " to reddit");
            }
        }
    }

    private SortedSet<String> findUrls(String message) {
        final Matcher matcher = URL_PATTERN.matcher(message);
        final ImmutableSortedSet.Builder<String> urls = ImmutableSortedSet.naturalOrder();
        while (matcher.find()) {
            urls.add(matcher.group());
        }

        return urls.build();
    }
}
