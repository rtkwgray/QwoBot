package com.sl5r0.qwobot.irc.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.command.Command;
import com.sl5r0.qwobot.domain.command.CommandHandler;
import com.sl5r0.qwobot.thirdparty.reddit.Reddit;
import com.sl5r0.qwobot.thirdparty.reddit.RedditPostFailedException;
import org.jsoup.Jsoup;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sl5r0.qwobot.core.IrcTextFormatter.BOLD;
import static com.sl5r0.qwobot.domain.command.Parameter.repeating;
import static com.sl5r0.qwobot.domain.command.Parameter.url;

@Singleton
public class UrlScanningService extends AbstractIrcEventService {
    private final Reddit reddit;

    @Inject
    public UrlScanningService(Reddit reddit) {
        this.reddit = checkNotNull(reddit, "reddit must not be null");
    }

    public void scanUrls(GenericMessageEvent event, List<String> urls) {
        for (String url : urls) {
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
                event.respond(BOLD.format(channelMessage));
            }

            try {
                reddit.post(pageTitle, url);
            } catch (RedditPostFailedException e) {
                log.info("Not posting " + url + " to reddit");
            }
        }
    }

    @Override
    protected void initialize() {
        registerCommand(Command.forEvent(GenericMessageEvent.class)
                .addParameters(repeating(url()))
                .description("Show the title for one or more URLs")
                .handler(new CommandHandler<GenericMessageEvent>() {
                    @Override
                    public void handle(GenericMessageEvent event, List<String> arguments) {
                        scanUrls(event, arguments);
                    }
                })
                .build());
    }
}
