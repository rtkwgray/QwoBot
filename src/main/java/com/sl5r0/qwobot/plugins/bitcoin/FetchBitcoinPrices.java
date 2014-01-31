package com.sl5r0.qwobot.plugins.bitcoin;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.sl5r0.qwobot.plugins.commands.ParameterizedTriggerCommand;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.copyOf;

class FetchBitcoinPrices extends ParameterizedTriggerCommand {
    private static final String CHANNEL_TRIGGER = "!btc";
    private static final GenericUrl BITPAY_URL = new GenericUrl("https://bitpay.com/api/rates");

    private final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(new HttpRequestInitializer() {
        @Override
        public void initialize(HttpRequest request) throws IOException {
            request.setParser(new JsonObjectParser(new JacksonFactory()));
        }
    });

    FetchBitcoinPrices() {
        super(CHANNEL_TRIGGER, TO_LOWERCASE);
    }

    public String getHelp() {
        return CHANNEL_TRIGGER + " <currency type> [ <currency type> ... ]";
    }

    @Override
    public void execute(MessageEvent event, List<String> arguments) {
        try {
            final Set<String> currencies = copyOf(arguments);
            final BitCoinPrices bitCoinPrices = getBitCoinPrices().filterBy(currencies);
            if (bitCoinPrices.isEmpty()) {
                event.getChannel().send().message("Sorry, I couldn't find any data.");
            } else {
                event.getChannel().send().message(bitCoinPrices.toString());
            }
        } catch (IOException e) {
            event.getChannel().send().message("Couldn't fetch prices right now. Try again later.");
        }
    }

    private BitCoinPrices getBitCoinPrices() throws IOException {
        return requestFactory.buildGetRequest(FetchBitcoinPrices.BITPAY_URL).execute().parseAs(BitCoinPrices.class);
    }
}
