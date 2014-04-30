package com.sl5r0.qwobot.irc.service.bitcoin;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.irc.command.CommandHandler;
import com.sl5r0.qwobot.irc.service.AbstractIrcEventService;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.IOException;
import java.util.List;

import static com.sl5r0.qwobot.irc.command.Command.forEvent;
import static com.sl5r0.qwobot.irc.command.Parameter.*;

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
                        .addParameters(literal("!btc"), repeating(string("currency code")))
                        .description("Check bitcoin prices")
                        .handler(new CommandHandler<GenericMessageEvent>() {
                            @Override
                            public void handle(GenericMessageEvent event, List<String> arguments) {
                                getPrices(event, arguments.subList(1, arguments.size()));
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
}
