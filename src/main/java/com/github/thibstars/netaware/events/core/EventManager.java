package com.github.thibstars.netaware.events.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Thibault Helsmoortel
 */
public class EventManager {

    private final Map<Class<? extends Event>, Set<EventHandler<? extends Event>>> eventListeners = new HashMap<>();

    public <E extends Event> void registerHandler(Class<E> eventType, EventHandler<E> eventHandler) {
        Set<EventHandler<? extends Event>> listeners = this.eventListeners.get(eventType);

        if (listeners == null) {
            HashSet<EventHandler<? extends Event>> newListeners = new HashSet<>();
            this.eventListeners.put(eventType, newListeners);
            newListeners.add(eventHandler);
        } else {
            listeners.add(eventHandler);
        }
    }

    public <E extends Event> void removeHandler(Class<E> eventType, EventHandler<E> eventHandler) {
        Set<EventHandler<? extends Event>> listeners = this.eventListeners.get(eventType);

        if (listeners != null) {
            listeners.remove(eventHandler);
        }
    }

    public <E extends Event> void removeAllHandlers(Class<E> eventType) {
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
