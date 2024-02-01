package com.github.thibstars.netaware.events;

import com.github.thibstars.netaware.scanners.PortScanner;
import java.net.InetAddress;

/**
 * @author Thibault Helsmoortel
 */
public class TcpIpPortFoundEvent extends PortScannerEvent {

    private final InetAddress ipAddress;
    private final Integer tcpIpPort;

    public TcpIpPortFoundEvent(PortScanner source, InetAddress ipAddress, Integer tcpIpPort) {
        super(source);
        this.ipAddress = ipAddress;
        this.tcpIpPort = tcpIpPort;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public Integer getTcpIpPort() {
        return tcpIpPort;
    }
}
