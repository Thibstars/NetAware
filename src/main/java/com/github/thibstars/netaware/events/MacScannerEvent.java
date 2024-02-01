package com.github.thibstars.netaware.events;

import com.github.thibstars.netaware.events.core.Event;
import com.github.thibstars.netaware.scanners.MacScanner;

/**
 * @author Thibault Helsmoortel
 */
public abstract class MacScannerEvent extends Event {

    protected MacScannerEvent(MacScanner source) {
        super(source);
    }
}
