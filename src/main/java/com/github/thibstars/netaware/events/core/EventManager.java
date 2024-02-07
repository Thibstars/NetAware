package com.github.thibstars.netaware.events.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager of events and handlers thereof.
 *
 * @author Thibault Helsmoortel
 */
public class EventManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventManager.class);

    private final Map<Class<? extends Event>, Set<EventHandler<? extends Event>>> eventHandlers = new HashMap<>();

    /**
     * Registers a handler for a given event type.
     *
     * @param eventType the event type for which to register the handler
     * @param eventHandler the handler to register
     * @param <E> the type of the event
     */
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

    /**
     * Removes a handler for a given event type.
     *
     * @param eventType the event type for which to remove the handler
     * @param eventHandler the handler to remove
     * @param <E> the type of the event
     */
    public <E extends Event> void removeHandler(Class<E> eventType, EventHandler<E> eventHandler) {
        LOGGER.info("Removing handler for type {}", eventType.getSimpleName());
        Set<EventHandler<? extends Event>> handlers = this.eventHandlers.get(eventType);

        if (handlers != null) {
            handlers.remove(eventHandler);
        }
    }

    /**
     * Removes all handlers for a given type.
     *
     * @param eventType the event type for which to remove all handlers
     * @param <E> the type of the event
     */
    public <E extends Event> void removeAllHandlers(Class<E> eventType) {
        LOGGER.info("Removing all handlers for type {}", eventType.getSimpleName());
        this.eventHandlers.remove(eventType);
    }

    /**
     * Dispatches a given event. This will notify the relevant registered event handlers.
     *
     * @param event the event to dispatch
     * @param <E> the type of the event
     */
    @SuppressWarnings("unchecked")
    public <E extends Event> void dispatch(E event) {
        var handlers = eventHandlers.get(event.getClass());
        if (handlers != null && !handlers.isEmpty()) {
            handlers.forEach(eventHandler -> ((EventHandler<E>) eventHandler).onEvent(event));
        }
    }

}
