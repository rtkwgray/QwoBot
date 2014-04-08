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
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.irc.service.runnables.MessageRunnable;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.sl5r0.qwobot.irc.service.MessageDispatcher.startingWithTrigger;

@Singleton
public class BitCoinService extends AbstractIdleService {
    private static final GenericUrl BITPAY_URL = new GenericUrl("https://bitpay.com/api/rates");

    private final MessageDispatcher messageDispatcher;
    private final EventBus eventBus;

    @Inject
    public BitCoinService(EventBus eventBus, MessageDispatcher messageDispatcher) {
        this.eventBus = checkNotNull(eventBus, "eventBus must not be null");
        this.messageDispatcher = checkNotNull(messageDispatcher, "messageDispatcher must not be null");
        this.messageDispatcher.subscribeToMessage(startingWithTrigger("!btc"), new CheckBitCoinPrices());
    }

    private class CheckBitCoinPrices implements MessageRunnable {
        private final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                request.setParser(new JsonObjectParser(new JacksonFactory()));
            }
        });

        @Override
        public void run(GenericMessageEvent<PircBotX> event, List<String> arguments) {
            try {
                final Set<String> currencies = copyOf(arguments);
                final BitCoinPrices bitCoinPrices = fetchBitCoinPrices().filterBy(currencies);
                if (bitCoinPrices.isEmpty()) {
                    event.respond("Sorry, I couldn't find any data.");
                } else {
                    event.respond(bitCoinPrices.toString());
                }
            } catch (IOException e) {
                event.respond("Couldn't fetch prices right now. Try again later.");
            }
        }

        private BitCoinPrices fetchBitCoinPrices() throws IOException {
            return requestFactory.buildGetRequest(BITPAY_URL).execute().parseAs(BitCoinPrices.class);
        }
    }

    @Override
    protected void startUp() throws Exception {
        eventBus.register(messageDispatcher);
    }

    @Override
    protected void shutDown() throws Exception {
        eventBus.unregister(messageDispatcher);
    }

    private static class BitCoinPrices extends ArrayList<BitCoinPrice> {
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

    private static class BitCoinPrice {
        @Key String code;
        @Key String name;
        @Key double rate;

        @Override
        public String toString() {
            return rate + " " + code;
        }
    }
}
