package com.github.thibstars.netaware.events;

import com.github.thibstars.netaware.scanners.IpScanner;
import com.github.thibstars.netaware.scanners.IpScannerInput;
import java.net.InetAddress;

/**
 * @author Thibault Helsmoortel
 */
public class IpAddressFoundEvent extends IpScannerEvent {

    private final IpScannerInput ipScannerInput;

    private final InetAddress ipAddress;


    public IpAddressFoundEvent(IpScanner source, IpScannerInput ipScannerInput, InetAddress ipAddress) {
        super(source);
        this.ipScannerInput = ipScannerInput;
        this.ipAddress = ipAddress;
    }

    public IpScannerInput getIpScannerInput() {
        return ipScannerInput;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }
}
