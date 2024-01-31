package com.github.thibstars.netaware.scanners;


import com.github.thibstars.netaware.events.Event;
import com.github.thibstars.netaware.events.EventListener;

/**
 * @author Thibault Helsmoortel
 */
public interface Scanner<I> {

    void scan(I input);

    void addEventListener(EventListener<Event> eventListener);

    void removeEventListener(EventListener<Event> eventListener);

}
