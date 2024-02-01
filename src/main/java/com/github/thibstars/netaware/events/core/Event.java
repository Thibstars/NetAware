package com.github.thibstars.netaware.events.core;

/**
 * @author Thibault Helsmoortel
 */
public abstract class Event {

    protected final Object source;

    protected Event(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
