package com.sl5r0.qwobot.plugins;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.core.BotConfiguration;
import com.sl5r0.qwobot.plugins.commands.Command;
import com.sl5r0.qwobot.plugins.exceptions.DuplicatePluginException;
import com.sl5r0.qwobot.plugins.exceptions.PluginNotRegisteredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.api.client.util.Maps.newHashMap;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Sets.newHashSet;

@Singleton
public class PluginManager {
    private static final Set<Plugin> registeredPlugins = newHashSet();
    private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
    private final EventBus eventBus;
    private final Injector injector;
    private final Map<String, Set<Command>> pluginCommands = newHashMap();
    private final List<Optional<Class<? extends Plugin>>> pluginsToLoad;

    @Inject
    public PluginManager(Injector injector, EventBus eventBus, BotConfiguration configuration) {
        this.pluginsToLoad = Lists.transform(configuration.getList("plugins.plugin[@class]"), toPluginClass);
        this.eventBus = checkNotNull(eventBus, "eventBus cannot be null");
        this.injector = checkNotNull(injector, "injector cannot be null");
    }

    public void initializePlugins() {
        for (Class<? extends Plugin> pluginClass : pluginsToLoad) {

            if (pluginClass == null) {
                continue;
            }

            try {
                registerPlugin(injector.getInstance(pluginClass));
            } catch (RuntimeException | DuplicatePluginException e) {
                log.error("Couldn't load plugin " + pluginClass.getSimpleName(), e);
            }
        }
    }

    private void registerPlugin(Plugin plugin) throws DuplicatePluginException {
        if (registeredPlugins.contains(plugin)) {
            throw new DuplicatePluginException(plugin.getName() + " is already loaded");
        }

        log.info("Loading plugin: " + plugin);
        registeredPlugins.add(plugin);

        final Set<Command> commands = plugin.getCommands();
        pluginCommands.put(plugin.getName().toLowerCase(), commands);

        for (Command command : commands) {
            eventBus.register(command);
            log.debug("Registered command: " + command.getHelp());
        }
    }

    // TODO: test for duplicate plugin names
    public Set<String> getRegisteredPlugins() {
        return copyOf(pluginCommands.keySet());
    }

    public Set<Command> getCommandsForPlugin(String pluginName) throws PluginNotRegisteredException {
        if (!pluginCommands.containsKey(pluginName)) {
            throw new PluginNotRegisteredException();
        }

        return copyOf(pluginCommands.get(pluginName));
    }

    private static final Function<Object, Class<? extends Plugin>> toPluginClass = new Function<Object, Optional<Class<? extends Plugin>>>() {
        public Optional<Class<? extends Plugin>> apply(Object o) {
            try {
                Class<?> pluginClass = Class.forName(o.toString());
                if (Plugin.class.isAssignableFrom(pluginClass)) {
                    Class<? extends Plugin> castedClass = pluginClass.asSubclass(Plugin.class);
                    return Optional.of(pluginClass.asSubclass(Plugin.class));
                } else {
                    log.error("Specified plugin class " + o.toString() + " doesn't extend Plugin and will not be loaded");
                    return null;
                }
            } catch (ClassNotFoundException e) {
                log.error("Plugin class not found: " + o.toString(), e);
                return null;
            }
        }
    };
}
