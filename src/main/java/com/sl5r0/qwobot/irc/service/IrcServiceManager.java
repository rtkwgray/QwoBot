package com.sl5r0.qwobot.irc.service;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.util.concurrent.Service.State;
import static com.google.common.util.concurrent.Service.State.NEW;

@Singleton
public class IrcServiceManager {
    private final Injector injector;
    private final Map<Class<? extends Service>, Service> services = newHashMap();

    @Inject
    public IrcServiceManager(Injector injector) {
        this.injector = injector;
    }

    public IrcServiceManager registerService(Class<? extends Service> service) {
        checkState(!services.containsKey(service), "service (" + service.getSimpleName() + ") is already registered");
        services.put(service, injector.getInstance(service));
        return this;
    }

    public void startService(Class<? extends Service> service) {
        checkState(services.containsKey(service), "service is not registered");
        services.get(service).startAsync();
        services.get(service).awaitRunning();
    }

    public void stopService(Class<? extends Service> service) {
        checkState(services.containsKey(service), "service is not registered");
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
