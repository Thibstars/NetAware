package com.github.thibstars.netaware.events;

import com.github.thibstars.netaware.scanners.IpScanner;

/**
 * @author Thibault Helsmoortel
 */
public class IpAddressFoundEvent extends Event {

    private String ipAddress;

    public IpAddressFoundEvent(IpScanner scanner) {
        super(scanner);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
