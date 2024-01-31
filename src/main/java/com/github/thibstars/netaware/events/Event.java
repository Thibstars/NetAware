package com.github.thibstars.netaware.events;

/**
 * @author Thibault Helsmoortel
 */
public abstract class Event {

    private EventListener<Event> eventListener;

    private final Object source;

    protected Event(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public final void addListener(final EventListener<Event> eventListener) {
        this.eventListener = eventListener;
    }

    public final void removeListener() {
        this.eventListener = null;
    }

    public void fire() {
        if (eventListener != null) {
            eventListener.eventFired(this);
        }
    }
}
