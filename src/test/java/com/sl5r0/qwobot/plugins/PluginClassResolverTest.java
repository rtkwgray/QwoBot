package com.sl5r0.qwobot.plugins;

import com.sl5r0.qwobot.core.BotConfiguration;
import com.sl5r0.qwobot.helpers.UnitTestHelpers;
import org.junit.Test;

import java.util.Set;

import static com.sl5r0.qwobot.helpers.ExtraMatchers.isClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

public class PluginClassResolverTest {

    @Test
    public void ensurePluginResolverCanFindValidPlugins() throws Exception {
        final BotConfiguration botConfiguration = UnitTestHelpers.configFromResource("qwobot-pluginClassResolver.xml");
        final PluginClassResolver resolver = new PluginClassResolver(botConfiguration);

        final Set<Class<? extends Plugin>> validPlugins = resolver.getValidPlugins();
        assertThat(validPlugins, hasSize(1));
        assertThat(validPlugins.iterator().next(), isClass(TestPlugin.class));

        final Set<String> invalidPlugins = resolver.getInvalidPlugins();
        assertThat(invalidPlugins, hasSize(2));
        assertThat(invalidPlugins, containsInAnyOrder("java.lang.String", "notAPlugin"));
    }
}
