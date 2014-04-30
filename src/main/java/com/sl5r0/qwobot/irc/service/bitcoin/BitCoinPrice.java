package com.sl5r0.qwobot.irc.service.bitcoin;

import com.google.api.client.util.Key;

public class BitCoinPrice {
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
