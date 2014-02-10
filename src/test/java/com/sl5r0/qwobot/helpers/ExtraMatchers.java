package com.sl5r0.qwobot.helpers;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;

public class ExtraMatchers {
    public static Matcher<Class<?>> isClass(final Class<?> expectedClass) {
        return new CustomTypeSafeMatcher<Class<?>>("class of type " + expectedClass.getName()) {
            @Override
            protected boolean matchesSafely(Class<?> aClass) {
                return aClass == expectedClass;
            }
        };
    }
}
