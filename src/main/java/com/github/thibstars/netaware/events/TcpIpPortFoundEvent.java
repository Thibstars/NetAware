package com.github.thibstars.netaware.events;

import com.github.thibstars.netaware.scanners.PortScanner;

/**
 * @author Thibault Helsmoortel
 */
public class TcpIpPortFoundEvent extends PortScannerEvent {

    private final String ipAddress;
    private final Integer tcpIpPort;

    public TcpIpPortFoundEvent(PortScanner source, String ipAddress, Integer tcpIpPort) {
        super(source);
        this.ipAddress = ipAddress;
        this.tcpIpPort = tcpIpPort;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Integer getTcpIpPort() {
        return tcpIpPort;
    }
}
