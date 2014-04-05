package com.sl5r0.qwobot.thirdparty.reddit;

import com.google.api.client.util.Key;

public class RedditResponse {
    @Key
    RedditLoginResponse json;

    String getCookie() {
        return json.data == null ? null : json.data.cookie;
    }

    String getModHash() {
        return json.data == null ? null : json.data.modhash;
    }
}
