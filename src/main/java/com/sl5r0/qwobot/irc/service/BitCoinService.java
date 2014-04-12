package com.sl5r0.qwobot.irc.service;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.api.client.util.Key;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.help.Command;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singleton;

@Singleton
public class BitCoinService extends AbstractIrcEventService {
    private static final Command CHECK_BTC = new Command("!btc", "Show current value of BTC in the specified currencies").addUnboundedParameter("currency code");
    private static final GenericUrl BITPAY_URL = new GenericUrl("https://bitpay.com/api/rates");
    private final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(new HttpRequestInitializer() {
        @Override
        public void initialize(HttpRequest request) throws IOException {
            request.setParser(new JsonObjectParser(new JacksonFactory()));
        }
    });

    protected BitCoinService() {
        super(singleton(CHECK_BTC));
    }

    @Subscribe
    public void btc(MessageEvent<PircBotX> event) {
        final List<String> currencies = argumentsFor(CHECK_BTC, event.getMessage());

        BitCoinPrices bitCoinPrices = new BitCoinPrices();
        try {
            bitCoinPrices = requestFactory.buildGetRequest(BITPAY_URL).execute().parseAs(BitCoinPrices.class).filterBy(currencies);
        } catch (IOException e) {
            event.respond("BTC conversion failed. Try again later.");
        }

        if (bitCoinPrices.isEmpty()) {
            event.respond("Sorry, I couldn't find any data.");
        } else {
            event.respond(bitCoinPrices.toString());
        }
    }

    public static class BitCoinPrices extends ArrayList<BitCoinPrice> {
        @Override
        public String toString() {
            return "1BTC = " + Joiner.on(" | ").join(this);
        }

        public BitCoinPrices filterBy(List<String> currencies) {
            BitCoinPrices filtered = new BitCoinPrices();
            for (BitCoinPrice bitCoinPrice : this) {
                if (currencies.contains(bitCoinPrice.code.toLowerCase())) {
                    filtered.add(bitCoinPrice);
                }
            }
            return filtered;
        }
    }

    public static class BitCoinPrice {
        @Key String code;
        @Key String name;
        @Key double rate;

        @Override
        public String toString() {
            return rate + " " + code;
        }
    }
}
