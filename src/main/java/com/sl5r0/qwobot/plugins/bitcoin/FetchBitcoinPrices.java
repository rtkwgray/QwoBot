package com.sl5r0.qwobot.plugins.bitcoin;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.collect.ImmutableSet;
import com.sl5r0.qwobot.plugins.commands.PrefixCommand;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.util.List;
import java.util.Set;

class FetchBitcoinPrices extends PrefixCommand {
    private static final String CHANNEL_TRIGGER = "!btc";
    private static final GenericUrl BITPAY_URL = new GenericUrl("https://bitpay.com/api/rates");

    private final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(new HttpRequestInitializer() {
        @Override
        public void initialize(HttpRequest request) throws IOException {
            request.setParser(new JsonObjectParser(new JacksonFactory()));
        }
    });

    FetchBitcoinPrices() {
        super(CHANNEL_TRIGGER);
    }

    public String getHelp() {
        return CHANNEL_TRIGGER + " <currency type> [ <currency type> ... ]";
    }

    @Override
    protected void execute(MessageEvent event, List<String> arguments) {
        try {
            final Set<String> currencies = ImmutableSet.copyOf(arguments);
            event.getBot().sendMessage(event.getChannel(), getBitCoinPrices().filterBy(currencies).toString());
        } catch (IOException e) {
            event.getBot().sendMessage(event.getChannel(), "Couldn't fetch prices right now. Try again later.");
        }
    }

    private BitCoinPrices getBitCoinPrices() throws IOException {
        return requestFactory.buildGetRequest(FetchBitcoinPrices.BITPAY_URL).execute().parseAs(BitCoinPrices.class);
    }
}
