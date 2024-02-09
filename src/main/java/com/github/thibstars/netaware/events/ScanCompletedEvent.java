package com.github.thibstars.netaware.events;

import com.github.thibstars.netaware.events.core.TimeStampedEvent;
import com.github.thibstars.netaware.scanners.Scanner;

/**
 * Event to be dispatched when a scan is completed.
 *
 * @author Thibault Helsmoortel
 */
public class ScanCompletedEvent<S extends Scanner<?>> extends TimeStampedEvent {

    public ScanCompletedEvent(S source) {
        super(source);
    }

    @SuppressWarnings("unchecked")
    public S getScanner() {
        return (S) source;
    }
}
