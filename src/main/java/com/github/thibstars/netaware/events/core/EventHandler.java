package com.github.thibstars.netaware.events.core;

/**
 * Contract for event handling.
 *
 * @author Thibault Helsmoortel
 */
public interface EventHandler<T extends Event> {

    /**
     * The action to perform when an event is dispatched.
     *
     * @param event the dispatched event
     */
    void onEvent(T event);
}
