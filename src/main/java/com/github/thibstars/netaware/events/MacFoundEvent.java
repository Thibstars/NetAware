package com.github.thibstars.netaware.events;

import com.github.thibstars.netaware.scanners.MacScanner;

/**
 * @author Thibault Helsmoortel
 */
public class MacFoundEvent extends MacScannerEvent {

    private final String ipAddress;

    private final String macAddress;

    public MacFoundEvent(MacScanner source, String ipAddress, String macAddress) {
        super(source);
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }
}
