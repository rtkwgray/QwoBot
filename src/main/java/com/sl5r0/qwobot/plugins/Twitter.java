package com.sl5r0.qwobot.plugins;

import com.sl5r0.qwobot.api.QwoBot;
import com.sl5r0.qwobot.api.QwoBotPlugin;
import org.pircbotx.Colors;
import twitter4j.*;

public class Twitter extends QwoBotPlugin {
    private static final long WR_RECORD_TWITTER_ID = 37104970;
    private static final long QW0RUM_TWITTER_ID = 1460330023;

    public Twitter(QwoBot qwoBot) {
        super(qwoBot);
        this.listen();
    }

    public void listen() {
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
            	long statusID = status.getUser().getId();
            	
            	if(!status.isRetweet() && (statusID == WR_RECORD_TWITTER_ID || statusID == QW0RUM_TWITTER_ID))
            	{
            		bot().sendMessageToAllChannels(Colors.OLIVE + status.getUser().getScreenName() + ": " + status.getText());
            	}
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }

            @Override
            public void onScrubGeo(long l, long l2) {
            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(listener);
        twitterStream.filter(new FilterQuery(new long[]{WR_RECORD_TWITTER_ID, QW0RUM_TWITTER_ID}));
    }

    @Override
    public String getDescription() {
        return "Real-time twitter client.";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public String getHelp() {
        return "This plugin does not support any commands.";
    }
}
