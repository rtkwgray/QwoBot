package com.sl5r0.qwobot.irc.service;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Maps.newConcurrentMap;
import static com.sl5r0.qwobot.core.IrcTextFormatter.BOLD;

@Singleton
public class UrlScanningService extends AbstractIrcEventService {
    private static final Pattern URL_PATTERN = Pattern.compile("(https?://\\S+\\.\\S+)");
    private final ConcurrentMap<String, String> titleCache = newConcurrentMap();
    
    @Inject
    public UrlScanningService(EventBus eventBus) {
        super(eventBus);
    }
    
    @Subscribe
    public void scanUrls(MessageEvent<PircBotX> event) {
        for (String url : findUrls(event.getMessage())) {
            try {
                final String pageTitle;
                if (titleCache.containsKey(url)) {
                    pageTitle = titleCache.get(url);
                } else {
                    try {
                        pageTitle = Jsoup.connect(url).get().title();
                        titleCache.put(url, pageTitle);
                    } catch (UnsupportedMimeTypeException e) {
                        log.info("Couldn't fetch a page title for " + url);
                        return;
                    }
                }

                final String channelMessage = pageTitle.length() > 400 ? pageTitle.substring(0, 400) + "..." : pageTitle;
                event.getChannel().send().message(BOLD.format(channelMessage));
            } catch (IOException e) {
                e.printStackTrace();
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
