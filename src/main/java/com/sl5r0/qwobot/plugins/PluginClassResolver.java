package com.sl5r0.qwobot.plugins;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;

public class PluginClassResolver {
    public static final String PLUGIN_CLASS_KEY = "plugins.plugin[@class]";

    private final Set<String> invalidPlugins = newHashSet();
    private final Set<Class<? extends Plugin>> validPlugins = newHashSet();

//    @Inject
//    public PluginClassResolver(BotConfiguration botConfiguration) {
//        resolvePlugins(asList(botConfiguration.getStringArray(PLUGIN_CLASS_KEY)));
//    }

    public Set<Class<? extends Plugin>> getValidPlugins() {
        return copyOf(validPlugins);
    }

    public Set<String> getInvalidPlugins() {
        return copyOf(invalidPlugins);
    }

    private void resolvePlugins(List<String> pluginClassNames) {
        final Iterable<PluginCandidateClass> candidateClasses = transform(pluginClassNames, new Function<String, PluginCandidateClass>() {
            @Override
            public PluginCandidateClass apply(String className) {
                return new PluginCandidateClass(className);
            }
        });

        for (PluginCandidateClass candidateClass : candidateClasses) {
            if (candidateClass.exists()) {
                validPlugins.add(candidateClass.pluginClass);
            } else {
                invalidPlugins.add(candidateClass.className);
            }
        }
    }

    private static final Function<String, Class<?>> toClass = new Function<String, Class<?>>() {
        @Nullable
        @Override
        public Class<?> apply(@Nullable String className) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
    };

    private static class PluginCandidateClass {
        private final Class<? extends Plugin> pluginClass;
        private final String className;

        private PluginCandidateClass(String className) {
            this.className = className;
            this.pluginClass = convertClassNameToPluginClass(className);
        }

        private boolean exists() {
            return pluginClass != null;
        }

        private Class<? extends Plugin> convertClassNameToPluginClass(String className) {
            final Class<?> actualClass = toClass.apply(className);
            if (actualClass != null && Plugin.class.isAssignableFrom(actualClass)) {
                return actualClass.asSubclass(Plugin.class);
            }
            return null;
        }
    }
}
