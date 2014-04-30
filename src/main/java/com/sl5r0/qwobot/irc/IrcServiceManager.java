package com.sl5r0.qwobot.irc;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.util.concurrent.Service.State;
import static com.google.common.util.concurrent.Service.State.NEW;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class IrcServiceManager {
    private static final Logger log = getLogger(IrcServiceManager.class);
    private final Injector injector;
    private final Map<Class<? extends Service>, Service> services = newHashMap();
    private final ExecutorService listenerExecutor = newFixedThreadPool(5);

    @Inject
    public IrcServiceManager(Injector injector) {
        this.injector = injector;
    }

    public IrcServiceManager registerService(final Class<? extends Service> service) {
        checkState(!services.containsKey(service), "service (" + service.getSimpleName() + ") is already registered");

        final Service instance = injector.getInstance(service);
        instance.addListener(new Service.Listener() {
            @Override
            public void failed(State from, Throwable failure) {
                log.error("Exception in service " + service.getName() + " while " + from, failure);
            }
        }, listenerExecutor);

        services.put(service, instance);
        return this;
    }

    public void startService(Class<? extends Service> service) {
        checkState(services.containsKey(service), "service (" + service.getSimpleName() + ") is not registered");
        services.get(service).startAsync();
        services.get(service).awaitRunning();
    }

    public void stopService(Class<? extends Service> service) {
        checkState(services.containsKey(service), "service (" + service.getSimpleName() + ") is not registered");
        services.get(service).stopAsync();
        services.get(service).awaitTerminated();
    }

    public void disposeService(Class<? extends Service> service) {
        stopService(service);
        services.remove(service);
    }

    public Map<String, State> getServices() {
        final ImmutableMap.Builder<String, State> servicesMap = ImmutableMap.builder();
        for (Service service : services.values()) {
            final int proxySignatureStartIndex = service.getClass().getSimpleName().indexOf("$$");
            final String unproxiedServiceName;
            if (proxySignatureStartIndex >= 0) {
                unproxiedServiceName = service.getClass().getSimpleName().substring(0, proxySignatureStartIndex);
            } else {
                unproxiedServiceName = service.getClass().getSimpleName();
            }
            servicesMap.put(unproxiedServiceName, service.state());
        }
        return servicesMap.build();
    }

    public void startAllUnstartedServices() {
        final Collection<Service> unstartedServices = Collections2.filter(services.values(), new Predicate<Service>() {
            @Override
            public boolean apply(Service input) {
                return input.state() == NEW;
            }
        });

        for (Service unstartedService : unstartedServices) {
            unstartedService.startAsync();
        }
    }
}
