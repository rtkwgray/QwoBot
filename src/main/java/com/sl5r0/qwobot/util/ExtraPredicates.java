package com.sl5r0.qwobot.util;

import com.google.common.base.Predicate;

public class ExtraPredicates {
    public static Predicate<String> matchesCaseInsensitiveString(final String string) {
        return new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input.toLowerCase().equals(string.toLowerCase());
            }
        };
    }
}
