package com.github.thibstars.netaware.events;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Thibault Helsmoortel
 */
public abstract class Event {

    private final Set<EventListener<Event>> eventListeners = new HashSet<>();

    private final Object source;

    protected Event(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public final void addListener(final EventListener<Event> eventListener) {
        this.eventListeners.add(eventListener);
    }

    public final void removeListener(final EventListener<Event> eventListener) {
        this.eventListeners.remove(eventListener);
    }

    public void fire() {
        eventListeners.forEach(eventListener -> eventListener.eventFired(this));
    }
}
