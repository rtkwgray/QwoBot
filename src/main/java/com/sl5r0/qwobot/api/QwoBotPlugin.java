package com.sl5r0.qwobot.api;

import com.google.common.collect.Lists;
import com.sl5r0.qwobot.domain.MessageEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class QwoBotPlugin implements Plugin {
    private static final String PARAMETER_REGEX = "\"([^\"]*)\"|(\\S+)";
    private static final Pattern PARAMETER_PATTERN = Pattern.compile(PARAMETER_REGEX);

    private final QwoBot qwoBot;

    protected QwoBotPlugin(QwoBot qwoBot) {
        this.qwoBot = qwoBot;
        this.qwoBot.registerPlugin(this);
        System.out.println("Loaded Plugin: " + getName());
    }

    protected final QwoBot bot() {
        return qwoBot;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public String getConfigurationInformation() {
        return null;
    }

    protected static List<String> getParametersFromEvent(MessageEvent event) {
        Matcher matcher = PARAMETER_PATTERN.matcher(event.message);
        List<String> parameters = Lists.newArrayList();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parameters.add(matcher.group(1));
            } else {
                parameters.add(matcher.group(2));
            }
        }
        return parameters;
    }
}
