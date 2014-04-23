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
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.command.CommandHandler;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sl5r0.qwobot.domain.command.Command.forEvent;
import static com.sl5r0.qwobot.domain.command.Parameter.exactMatch;
import static com.sl5r0.qwobot.domain.command.Parameter.string;

@Singleton
public class BitCoinService extends AbstractIrcEventService {
    private static final GenericUrl BITPAY_URL = new GenericUrl("https://bitpay.com/api/rates");
    private final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(new HttpRequestInitializer() {
        @Override
        public void initialize(HttpRequest request) throws IOException {
            request.setParser(new JsonObjectParser(new JacksonFactory()));
        }
    });

    @Override
    protected void initialize() {
        registerCommand(
                forEvent(GenericMessageEvent.class)
                        .addParameter(exactMatch("!btc"))
                        .addParameter(string("currency code"))
                        .description("Check bitcoin prices")
                        .handler(new CommandHandler<GenericMessageEvent>() {
                            @Override
                            public void handle(GenericMessageEvent event, List<String> arguments) {
                                getPrices(event, Collections.singletonList(arguments.get(1)));
                            }
                        })
                        .build()
        );
    }

    public void getPrices(GenericMessageEvent event, List<String> currencies) {
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
}
