package com.github.thibstars.netaware.events;

import com.github.thibstars.netaware.scanners.IpScanner;

/**
 * @author Thibault Helsmoortel
 */
public abstract class IpScannerEvent extends Event {

    protected IpScannerEvent(IpScanner source) {
        super(source);
    }
}
