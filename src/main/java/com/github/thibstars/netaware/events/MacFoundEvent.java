package com.github.thibstars.netaware.events;

import com.github.thibstars.netaware.scanners.MacScanner;
import java.net.InetAddress;

/**
 * Event to be dispatched when a MAC address is found.
 *
 * @author Thibault Helsmoortel
 */
public class MacFoundEvent extends MacScannerEvent {

    private final InetAddress ipAddress;

    private final String macAddress;

    public MacFoundEvent(MacScanner source, InetAddress ipAddress, String macAddress) {
        super(source);
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }
}
