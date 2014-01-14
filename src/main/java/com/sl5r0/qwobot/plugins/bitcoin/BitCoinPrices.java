package com.sl5r0.qwobot.plugins.bitcoin;

import com.google.api.client.repackaged.com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Set;

public class BitCoinPrices extends ArrayList<BitCoinPrice> {
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
