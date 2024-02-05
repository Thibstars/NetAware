package com.github.thibstars.netaware.events.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class EventManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(EventManager.class);

    private final Map<Class<? extends Event>, Set<EventHandler<? extends Event>>> eventListeners = new HashMap<>();

    public <E extends Event> void registerHandler(Class<E> eventType, EventHandler<E> eventHandler) {
        LOGGER.info("Registering handler for type {}", eventType.getSimpleName());
        Set<EventHandler<? extends Event>> listeners = this.eventListeners.get(eventType);

        if (listeners == null) {
            Set<EventHandler<? extends Event>> newListeners = new HashSet<>();
            this.eventListeners.put(eventType, newListeners);
            newListeners.add(eventHandler);
        } else {
            listeners.add(eventHandler);
        }
    }

    public <E extends Event> void removeHandler(Class<E> eventType, EventHandler<E> eventHandler) {
        LOGGER.info("Removing handler for type {}", eventType.getSimpleName());
        Set<EventHandler<? extends Event>> listeners = this.eventListeners.get(eventType);

        if (listeners != null) {
            listeners.remove(eventHandler);
        }
    }

    public <E extends Event> void removeAllHandlers(Class<E> eventType) {
        LOGGER.info("Removing all handlers for type {}", eventType.getSimpleName());
        this.eventListeners.remove(eventType);
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> void dispatch(E event) {
        var listeners = eventListeners.get(event.getClass());
        if (listeners != null && !listeners.isEmpty()) {
            listeners.forEach(eventHandler -> ((EventHandler<E>) eventHandler).onEvent(event));
        }
    }

}
