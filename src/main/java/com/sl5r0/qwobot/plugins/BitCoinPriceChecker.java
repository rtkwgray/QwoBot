package com.sl5r0.qwobot.plugins;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.api.client.util.Key;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;
import com.sl5r0.qwobot.api.QwoBotPlugin;
import com.sl5r0.qwobot.core.QwoBot;
import com.sl5r0.qwobot.domain.MessageEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class BitCoinPriceChecker extends QwoBotPlugin {
    private static final GenericUrl BITPAY_URL = new GenericUrl("https://bitpay.com/api/rates");
    private static final String CHANNEL_TRIGGER = "!btc";

    public BitCoinPriceChecker(QwoBot qwoBot) {
        super(qwoBot);
    }

    private final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(new HttpRequestInitializer() {
        @Override
        public void initialize(HttpRequest request) throws IOException {
            request.setParser(new JsonObjectParser(new JacksonFactory()));
        }
    });

    @Subscribe
    public void processMessageEvent(MessageEvent event) {
        if (event.message().startsWith(CHANNEL_TRIGGER)) {
            final Iterable<String> arguments = Splitter.on(' ')
                    .omitEmptyStrings()
                    .trimResults()
                    .split(event.message().substring(CHANNEL_TRIGGER.length()));

            try {
                final Set<String> currencies = ImmutableSet.copyOf(arguments);
                bot().sendMessageToAllChannels(getBitCoinPrices().filterBy(currencies).toString());
            } catch (IOException e) {
                bot().sendMessageToAllChannels("Couldn't fetch prices right now. Try again later.");
            }
        }
    }

    @Override
    public String getDescription() {
        return "Shows bitcoin prices in various currencies, based on bitpay's API.";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getHelp() {
        return "!btc <currency type> [ <currency type> ... ]";
    }

    public static class BitCoinPrice {
        @Key
        String code;
        @Key
        String name;
        @Key
        double rate;

        @Override
        public String toString() {
            return rate + " " + code;
        }
    }

    public static class BitCoinPrices extends ArrayList<BitCoinPrice> {
        @Override
        public String toString() {
            return "1BTC = " + Joiner.on(" | ").join(this);
        }

        public BitCoinPrices filterBy(Set<String> currencies) {
            BitCoinPrices filtered = new BitCoinPrices();
            for (BitCoinPrice bitCoinPrice : this) {
                if (currencies.contains(bitCoinPrice.code.toLowerCase())) {
                    filtered.add(bitCoinPrice);
                }
            }
            return filtered;
        }
    }

    private BitCoinPrices getBitCoinPrices() throws IOException {
        return requestFactory.buildGetRequest(BITPAY_URL).execute().parseAs(BitCoinPrices.class);
    }
}
