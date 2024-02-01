package com.github.thibstars.netaware.events;

/**
 * @author Thibault Helsmoortel
 */
public abstract class Event {

    private final Object source;

    protected Event(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
