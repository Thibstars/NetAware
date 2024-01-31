package com.github.thibstars.netaware.events;

import com.github.thibstars.netaware.scanners.IpScanner;

/**
 * @author Thibault Helsmoortel
 */
public class IpAddressFoundEvent extends IpScannerEvent {

    private final String ipAddress;

    public IpAddressFoundEvent(IpScanner source, String ipAddress) {
        super(source);
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
