package com.github.thibstars.netaware.events.core;

/**
 * @author Thibault Helsmoortel
 */
public interface EventHandler<T extends Event> {

    void onEvent(T event);
}
