package com.github.thibstars.netaware.events.core;

/**
 * Representation of an event.
 *
 * @author Thibault Helsmoortel
 */
public abstract class Event {

    /**
     * The source object triggering the event.
     */
    protected final Object source;

    protected Event(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
