package com.github.thibstars.netaware.events;

/**
 * @author Thibault Helsmoortel
 */
public interface EventListener<T extends Event> {

    void eventFired(T event);
}
