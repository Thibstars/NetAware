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

    private static final Logger LOGGER = LoggerFactory.getLogger(EventManager.class);

    private final Map<Class<? extends Event>, Set<EventHandler<? extends Event>>> eventHandlers = new HashMap<>();

    public <E extends Event> void registerHandler(Class<E> eventType, EventHandler<E> eventHandler) {
        LOGGER.info("Registering handler for type {}", eventType.getSimpleName());
        Set<EventHandler<? extends Event>> listeners = this.eventHandlers.get(eventType);

        if (listeners == null) {
            Set<EventHandler<? extends Event>> newHandlers = new HashSet<>();
            this.eventHandlers.put(eventType, newHandlers);
            newHandlers.add(eventHandler);
        } else {
            listeners.add(eventHandler);
        }
    }

    public <E extends Event> void removeHandler(Class<E> eventType, EventHandler<E> eventHandler) {
        LOGGER.info("Removing handler for type {}", eventType.getSimpleName());
        Set<EventHandler<? extends Event>> handlers = this.eventHandlers.get(eventType);

        if (handlers != null) {
            handlers.remove(eventHandler);
        }
    }

    public <E extends Event> void removeAllHandlers(Class<E> eventType) {
        LOGGER.info("Removing all handlers for type {}", eventType.getSimpleName());
        this.eventHandlers.remove(eventType);
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> void dispatch(E event) {
        var listeners = eventHandlers.get(event.getClass());
        if (listeners != null && !listeners.isEmpty()) {
            listeners.forEach(eventHandler -> ((EventHandler<E>) eventHandler).onEvent(event));
        }
    }

}
