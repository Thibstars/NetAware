package com.github.thibstars.netaware.events;

import com.github.thibstars.netaware.scanners.PortScanner;

/**
 * @author Thibault Helsmoortel
 */
public abstract class PortScannerEvent extends Event {

    protected PortScannerEvent(PortScanner source) {
        super(source);
    }
}
