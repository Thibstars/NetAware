package com.github.thibstars.netaware.events;

import com.github.thibstars.netaware.scanners.IpScanner;
import java.net.InetAddress;

/**
 * @author Thibault Helsmoortel
 */
public class IpAddressFoundEvent extends IpScannerEvent {

    private final InetAddress ipAddress;

    public IpAddressFoundEvent(IpScanner source, InetAddress ipAddress) {
        super(source);
        this.ipAddress = ipAddress;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }
}
