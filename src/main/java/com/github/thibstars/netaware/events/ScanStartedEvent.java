package com.github.thibstars.netaware.events;

import com.github.thibstars.netaware.events.core.TimeStampedEvent;
import com.github.thibstars.netaware.scanners.Scanner;

/**
 * Event to be dispatched when a scan is started.
 *
 * @author Thibault Helsmoortel
 */
public class ScanStartedEvent<S extends Scanner<?>> extends TimeStampedEvent {

    public ScanStartedEvent(S source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    public S getScanner() {
        return (S) source;
    }
}
