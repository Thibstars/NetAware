package com.github.thibstars.netaware.events.core;

import java.time.Instant;

/**
 * Representation of an event that occurred at a specific point in time.
 *
 * @author Thibault Helsmoortel
 */
public abstract class TimeStampedEvent extends Event {

    private final Instant instant;

    protected TimeStampedEvent(Object source) {
        super(source);

        this.instant = Instant.now();
    }

    public Instant getInstant() {
        return instant;
    }
}
