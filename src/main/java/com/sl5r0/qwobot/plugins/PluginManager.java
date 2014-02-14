package com.sl5r0.qwobot.plugins;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.plugins.commands.Command;
import com.sl5r0.qwobot.plugins.exceptions.DuplicatePluginException;
import com.sl5r0.qwobot.plugins.exceptions.PluginNotRegisteredException;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Sets.newHashSet;

@Singleton
public class PluginManager {
    private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
    private final EventBus eventBus;
    private final Injector injector;
    private final PluginClassResolver pluginClassResolver;
    private final Set<Plugin> registeredPlugins = newHashSet();

    @Inject
    public PluginManager(Injector injector, EventBus eventBus, PluginClassResolver pluginClassResolver) {
        this.pluginClassResolver = checkNotNull(pluginClassResolver, "pluginClassResolver cannot be null");
        this.eventBus = checkNotNull(eventBus, "eventBus cannot be null");
        this.injector = checkNotNull(injector, "injector cannot be null");
    }

    public void initializePlugins() {
        for (String pluginClass : pluginClassResolver.getInvalidPlugins()) {
            log.warn("Could not find plugin class: " + pluginClass);
        }

        for (Class<? extends Plugin> pluginClass : pluginClassResolver.getValidPlugins()) {
            final Plugin plugin;
            try {
                plugin = injector.getInstance(pluginClass);
            } catch (RuntimeException e) {
                if (e.getCause() != null && e.getCause() instanceof ConfigurationException) {
                    log.error("Error initializing plugin: " + pluginClass.getSimpleName() + ": " + e.getCause().getMessage());
                } else {
                    log.error("Error initializing plugin: " + pluginClass.getSimpleName(), e);
                }
                continue;
            }

            try {
                registerPlugin(plugin);
            } catch (RuntimeException | DuplicatePluginException e) {
                log.error("Could not register plugin: ", e);
            }
        }
    }

    public Set<Plugin> getRegisteredPlugins() {
        return copyOf(registeredPlugins);
    }

    private void registerPlugin(Plugin plugin) throws DuplicatePluginException {
        if (registeredPlugins.contains(plugin)) {
            throw new DuplicatePluginException(plugin.getName() + " has already been registered");
        }

        log.info("Registering plugin: " + plugin.getName());
        registeredPlugins.add(plugin);

        final Set<Command> commands = plugin.getCommands();
        for (Command command : commands) {
            eventBus.register(command);
            for (String commandString : command.getHelp()) {
                log.debug("Registered command: " + commandString);
            }
        }
    }

    public Set<Command> getCommandsForPlugin(String pluginName) throws PluginNotRegisteredException {
        final Optional<Plugin> plugin = tryFind(registeredPlugins, hasName(pluginName));
        if (plugin.isPresent()) {
            return copyOf(plugin.get().getCommands());
        }

        throw new PluginNotRegisteredException("Could not find plugin + " + pluginName);
    }

    private static Predicate<Plugin> hasName(final String name) {
        return new Predicate<Plugin>() {
            @Override
            public boolean apply(Plugin plugin) {
                return plugin.getName().equals(name);
            }
        };
    }
}
